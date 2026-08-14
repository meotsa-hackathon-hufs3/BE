package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.ColorType
import com.meotsa.creation.entity.MaterialType
import com.meotsa.creation.entity.ModelOption
import com.meotsa.creation.entity.ProductType

data class JobCreateRequest(
    val productType: ProductType,
    val size: Int,
    val amount: Int,
    val material: MaterialType,
    val color: ColorType,
) {
    fun toModelOption() = ModelOption(productType, size, amount, material, color)
}
