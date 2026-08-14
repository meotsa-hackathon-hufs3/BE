package com.meotsa.creation.dto.message

import com.meotsa.creation.entity.ColorType
import com.meotsa.creation.entity.MaterialType
import com.meotsa.creation.entity.ModelOption
import com.meotsa.creation.entity.ProductType

data class JobMessage(
    val jobId: Long,
    val creationId: Long,
    val stylizedImageURL: String,
    val productType: ProductType,
    val amount: Int,
    val material: MaterialType,
    val color: ColorType,
) {
    companion object {
        fun of(
            jobId: Long,
            creationId: Long,
            stylizedImageURL: String,
            modelOption: ModelOption,
        ) = JobMessage(
            jobId = jobId,
            creationId = creationId,
            stylizedImageURL = stylizedImageURL,
            productType = modelOption.productType,
            amount = modelOption.amount,
            material = modelOption.material,
            color = modelOption.color,
        )
    }
}
