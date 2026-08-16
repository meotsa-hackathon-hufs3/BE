package com.meotsa.printshop.entity

import com.meotsa.creation.entity.MaterialType as CreationMaterialType

enum class MaterialType {
    PLA,
    RESIN,
    ;

    companion object {
        fun from(material: CreationMaterialType) =
            when (material) {
                CreationMaterialType.PLA -> PLA
                CreationMaterialType.RESIN -> RESIN
            }
    }
}
