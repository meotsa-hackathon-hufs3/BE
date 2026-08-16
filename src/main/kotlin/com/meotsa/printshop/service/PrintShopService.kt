package com.meotsa.printshop.service

import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.creation.repository.CreationRepository
import com.meotsa.global.exception.BusinessException
import com.meotsa.printshop.dto.response.PrintShopEstimateResponse
import com.meotsa.printshop.entity.MaterialType
import com.meotsa.printshop.exception.PrintShopErrorCode
import com.meotsa.printshop.repository.PrintShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PrintShopService(
    private val creationRepository: CreationRepository,
    private val printShopRepository: PrintShopRepository,
) {
    fun getEstimates(creationId: Long): List<PrintShopEstimateResponse> {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)
        val modelOption =
            creation.option
                ?: throw BusinessException(PrintShopErrorCode.MODEL_OPTION_NOT_READY)

        return printShopRepository
            .findOptionsByMaterial(MaterialType.from(modelOption.material))
            .map { PrintShopEstimateResponse.of(it.printShop, it, printingCost = 0) } // 견적 로직 필요
    }
}
