package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.ColorType
import com.meotsa.creation.entity.MaterialType
import com.meotsa.creation.entity.ModelOption
import com.meotsa.creation.entity.ProductType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class JobCreateRequest(
    val productType: ProductType,
    val size: Int,
    @field:Min(1)
    @field:Max(100)
    val amount: Int,
    val material: MaterialType,
    val color: ColorType,
) {
    fun toModelOption() = ModelOption(productType, size, amount, material, color)
}
