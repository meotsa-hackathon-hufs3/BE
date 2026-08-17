package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.JobStatus
import com.meotsa.creation.entity.ModelGeometry

data class JobResultRequest(
    val status: JobStatus,
    val modelKey: String? = null,
    val volumeMm3: Double? = null,
    val bboxX: Double? = null,
    val bboxY: Double? = null,
    val bboxZ: Double? = null,
    val structureCheck: Boolean? = null,
    val widthCheck: Boolean? = null,
    val expectedFee: Int? = null,
    val error: String? = null,
) {
    fun toModelGeometry(): ModelGeometry? {
        if (volumeMm3 == null || bboxX == null || bboxY == null || bboxZ == null) return null
        return ModelGeometry(volumeMm3, bboxX, bboxY, bboxZ)
    }
}
