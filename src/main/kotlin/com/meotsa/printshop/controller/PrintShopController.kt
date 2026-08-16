package com.meotsa.printshop.controller

import com.meotsa.printshop.docs.PrintShopSwaggerSpec
import com.meotsa.printshop.dto.response.PrintShopEstimateResponse
import com.meotsa.printshop.service.PrintShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/creations/{creationId}/estimates")
class PrintShopController(
    private val printShopService: PrintShopService,
) : PrintShopSwaggerSpec {
    @GetMapping
    override fun getEstimates(
        @PathVariable creationId: Long,
    ): ResponseEntity<List<PrintShopEstimateResponse>> =
        ResponseEntity
            .ok()
            .body(printShopService.getEstimates(creationId))
}
