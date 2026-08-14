package com.meotsa.creation.entity

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class ModelOption(
    @Enumerated(EnumType.STRING)
    val productType: ProductType,
    val size: Int,
    val amount: Int,
    @Enumerated(EnumType.STRING)
    val material: MaterialType,
    @Enumerated(EnumType.STRING)
    val color: ColorType,
)
