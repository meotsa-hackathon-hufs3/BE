package com.meotsa.printshop.dto.response

import com.meotsa.printshop.entity.MaterialType
import com.meotsa.printshop.entity.PrintShop
import com.meotsa.printshop.entity.PrintShopOption
import com.meotsa.printshop.entity.ProcessType

data class PrintShopEstimateResponse(
    val printShopId: Long,
    val name: String,
    val tag: String,
    val material: MaterialType,
    val process: ProcessType,
    val expectedTime: String,
    val minQuantity: Int,
    val printingCost: Int,
    val shippingCost: Int,
    val totalCost: Int,
) {
    companion object {
        fun of(
            printShop: PrintShop,
            option: PrintShopOption,
            printingCost: Int,
        ): PrintShopEstimateResponse =
            PrintShopEstimateResponse(
                printShopId = printShop.id!!,
                name = printShop.name,
                tag = printShop.tag,
                material = option.material,
                process = option.processType,
                expectedTime = option.expectedTime,
                minQuantity = printShop.minQuantity,
                printingCost = printingCost,
                shippingCost = printShop.shippingCost,
                totalCost = printingCost + printShop.shippingCost,
            )
    }
}
