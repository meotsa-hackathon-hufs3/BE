package com.meotsa.creation.dto.response

import com.meotsa.creation.entity.ColorType
import com.meotsa.creation.entity.Job
import com.meotsa.creation.entity.JobStatus
import com.meotsa.creation.entity.MaterialType
import com.meotsa.creation.entity.ProductType

data class JobStatusResponse(
    val status: JobStatus,
    val productType: ProductType,
    val size: Int,
    val amount: Int,
    val material: MaterialType,
    val color: ColorType,
    val modelUrl: String? = null,
    val structureCheck: Boolean? = null,
    val widthCheck: Boolean? = null,
    val expectedFee: Int? = null,
    val error: String? = null,
) {
    companion object {
        fun of(
            job: Job,
            modelUrl: String?,
        ) = JobStatusResponse(
            status = job.status,
            productType = job.option.productType,
            size = job.option.size,
            amount = job.option.amount,
            material = job.option.material,
            color = job.option.color,
            modelUrl = modelUrl,
            structureCheck = job.structureCheck,
            widthCheck = job.widthCheck,
            expectedFee = job.expectedFee,
            error = job.error,
        )
    }
}
