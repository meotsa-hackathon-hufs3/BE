package com.meotsa.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    val region: String,
    val s3: S3,
    val sqs: Sqs,
    val cloudfront: CloudFront,
    val credentials: Credentials,
) {
    data class S3(
        val bucket: String,
    )

    data class Sqs(
        val queueUrl: String,
    )

    data class CloudFront(
        val domain: String,
    ) {
        fun urlOf(key: String) = "https://$domain/$key"
    }

    data class Credentials(
        val accessKey: String,
        val secretKey: String,
    )
}
