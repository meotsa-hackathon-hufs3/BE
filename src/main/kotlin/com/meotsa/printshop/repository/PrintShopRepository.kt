package com.meotsa.printshop.repository

import com.meotsa.printshop.entity.MaterialType
import com.meotsa.printshop.entity.PrintShop
import com.meotsa.printshop.entity.PrintShopOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PrintShopRepository : JpaRepository<PrintShop, Long> {
    @Query("select o from PrintShopOption o join fetch o.printShop where o.material = :material")
    fun findOptionsByMaterial(
        @Param("material") material: MaterialType,
    ): List<PrintShopOption>
}
