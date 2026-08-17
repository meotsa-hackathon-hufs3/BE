from pathlib import Path

from diagrams import Cluster, Diagram, Edge
from diagrams.aws.integration import SQS
from diagrams.aws.network import CloudFront
from diagrams.aws.storage import S3
from diagrams.custom import Custom
from diagrams.onprem.client import Users
from diagrams.onprem.compute import Server
from diagrams.onprem.database import Mysql
from diagrams.programming.framework import Spring

GRAPH_ATTR = {
    "bgcolor": "#f7f9fc",
    "compound": "true",
    "dpi": "192",
    "fontname": "Arial",
    "fontsize": "26",
    "labelloc": "t",
    "nodesep": "0.8",
    "ordering": "out",
    "pad": "0.5",
    "ranksep": "1.6",
    "splines": "spline",
}

NODE_ATTR = {
    "fontname": "Arial",
    "fontsize": "12",
    "height": "1.1",
    "margin": "0.14",
}

EDGE_ATTR = {
    "color": "#94a3b8",
    "fontcolor": "#475569",
    "fontname": "Arial",
    "fontsize": "11",
    "penwidth": "1.4",
}

DOCS = Path(__file__).resolve().parent

SYNC = {"color": "#2563eb", "fontcolor": "#1d4ed8", "penwidth": "1.8"}
ASYNC = {"color": "#ea580c", "fontcolor": "#c2410c", "penwidth": "2.2"}
STORAGE = {"color": "#64748b", "fontcolor": "#475569", "style": "dashed"}


def render(filename: Path) -> None:
    with Diagram(
        "Architecture Diagram",
        filename=str(filename),
        direction="TB",
        show=False,
        outformat="png",
        graph_attr=GRAPH_ATTR,
        node_attr=NODE_ATTR,
        edge_attr=EDGE_ATTR,
    ):
        # tier 1
        users = Users("Web Client")

        # tier 2
        with Cluster("Backend Server | Docker"):
            api = Spring("Spring Boot :8080")
            mysql = Mysql("MySQL")

        with Cluster("AWS"):
            queue = SQS("SQS FIFO")

        with Cluster("AI Server"):
            worker = Server("SQS long polling")
            mesh = Server("image -> STL")

        cdn = CloudFront("CloudFront")

        # tier 3
        bucket = S3("S3")
        openai = Custom("gpt-image-2", str(DOCS / "gpt.png"))

        free = {"constraint": "false"}

        # 티어 골격. graph ordering=out 이라 선언 순서가 좌 -> 우 배치 순서가 된다.
        users >> Edge(label="REST", **SYNC) >> api
        users >> Edge(style="invis") >> queue
        users >> Edge(style="invis") >> worker
        users >> Edge(style="invis") >> mesh
        users >> Edge(label="presigned PUT\nCDN GET", **STORAGE) >> cdn

        # tier 2 -> tier 3 (EC2 클러스터는 MySQL까지 세로로 걸친다)
        api >> Edge(label="JDBC", **STORAGE) >> mysql
        api >> Edge(label="presign / object I/O", **STORAGE) >> bucket
        api >> Edge(label="OpenAI Images API", **SYNC) >> openai
        mesh >> Edge(label="upload STL", **STORAGE) >> bucket
        cdn >> Edge(label="origin fetch", **STORAGE) >> bucket

        # tier 2 내부
        api >> Edge(label="enqueue JobMessage", **ASYNC, **free) >> queue
        queue >> Edge(label="poll", **ASYNC, **free) >> worker
        worker >> Edge(label="generate", **ASYNC, **free) >> mesh
        worker >> Edge(label="GET image", **STORAGE, **free) >> cdn
        worker >> Edge(label="callback", **ASYNC, **free) >> api


if __name__ == "__main__":
    render(DOCS / "architecture")
