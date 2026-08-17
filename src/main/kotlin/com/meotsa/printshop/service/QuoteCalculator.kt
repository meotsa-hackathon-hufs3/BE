package com.meotsa.printshop.service

import com.meotsa.creation.entity.ModelGeometry
import com.meotsa.global.exception.BusinessException
import com.meotsa.printshop.entity.PrintShopOption
import com.meotsa.printshop.entity.ProcessType
import com.meotsa.printshop.exception.PrintShopErrorCode
import org.springframework.stereotype.Component
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val VAT_RATE = 0.1
private const val ROUNDING_UNIT = 1000

// 부엉이공장 기준 프린터 세팅 가정
private const val PLA_DENSITY = 1.24 // g/cm^3
private const val USE_RATIO = 0.5 // FDM 인필
private const val GRAMS_PER_HOUR = 45 // FDM 토출량
private const val RESIN_DENSITY = 1.3 // g/cm^3
private val TILT_RAD = Math.toRadians(40.0) // 레진 배치 기울기(시간 계산용)

private data class PrintStats(
    val weightG: Double,
    val timeH: Double,
)

/**
 * 산식 출처는 부엉이공장(owlfactory.co.kr) 공개 견적기
 *
 * 밀도·레이어 두께·노광 시간·VAT·절상 단위는 상수로 두고,
 * 업체별로 갈리는 값만 PrintShopOption 컬럼에 있다.
 */
@Component
class QuoteCalculator {
    fun calculate(
        geometry: ModelGeometry,
        quantity: Int,
        option: PrintShopOption,
    ): Int {
        validate(option)

        val stats = derive(geometry, option.processType)
        val variableCost = unitVariableCost(stats, option) * quantity

        return when (option.processType) {
            ProcessType.FDM ->
                vatAndRound(variableCost + setupFeeTotal(quantity, option.baseFee) + option.dataFee)
            // 레진은 VAT·절상을 끝낸 뒤 개당 하한을 적용한다.
            ProcessType.SLA ->
                max(vatAndRound(variableCost + option.dataFee), option.minPricePerUnit * quantity)
        }
    }

    // 레진은 변동비의 대부분이 재료비라, pricePerGram 이 비면 unitVariableCost 의 `?: 0` 이
    // 금액을 조용히 반토막 낸다. 반대로 공정이 안 쓰는 컬럼은 값이 뭐든 결과가 같아 걸러내지 않는다.
    private fun validate(option: PrintShopOption) {
        if (option.processType == ProcessType.SLA && option.pricePerGram == null) {
            throw BusinessException(PrintShopErrorCode.INVALID_PRINT_SHOP_OPTION)
        }
    }

    private fun derive(
        geometry: ModelGeometry,
        process: ProcessType,
    ): PrintStats {
        val vol = geometry.volumeMm3 / 1000.0

        return when (process) {
            ProcessType.FDM -> {
                // 밀도 1.24 x 인필 0.5 x 서포트/스커트 여유 1.6
                val weight = vol * PLA_DENSITY * USE_RATIO * 1.6
                // 45g/h 로 뽑고 2.5배 여유
                PrintStats(weight, (weight / GRAMS_PER_HOUR) * 2.5)
            }

            ProcessType.SLA -> {
                // 밀도 1.3 x 서포트 1.30 x 여유 1.05 (항상 솔리드 기준)
                val weight = vol * RESIN_DENSITY * 1.30 * 1.05
                // 40도로 눕힌 높이 기준. 레이어 0.05mm, 초기 5레이어 37초 + 이후 6.2초
                val longAxis = max(geometry.bboxX, geometry.bboxY)
                val tiltedHeight = longAxis * sin(TILT_RAD) + geometry.bboxZ * cos(TILT_RAD) + 5
                val totalLayers = ceil((tiltedHeight + 5) / 0.05)
                PrintStats(weight, ((5 * 37) + ((totalLayers - 5) * 6.2)) / 3600)
            }
        }
    }

    private fun unitVariableCost(
        stats: PrintStats,
        option: PrintShopOption,
    ): Double = (stats.weightG * (option.pricePerGram ?: 0)) + (stats.timeH * option.pricePerHour)

    // 개수를 하나씩 세며 구간 비율을 적용한다(계단식).
    private fun setupFeeTotal(
        totalQty: Int,
        baseFee: Int,
    ): Double =
        (1..totalQty).sumOf { i ->
            when {
                i == 1 -> baseFee.toDouble()
                i <= 5 -> baseFee * 0.85
                i <= 10 -> baseFee * 0.7
                else -> baseFee * 0.5
            }
        }

    private fun vatAndRound(noVat: Double): Int = (ceil((noVat * (1 + VAT_RATE)) / ROUNDING_UNIT) * ROUNDING_UNIT).toInt()
}
