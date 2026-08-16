package com.meotsa.printshop.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "print_shop_option")
class PrintShopOption(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "print_shop_id", nullable = false)
    val printShop: PrintShop,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val material: MaterialType,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val processType: ProcessType,
    @Column(nullable = false)
    var expectedTime: String,
    @Column
    var pricePerGram: Int? = null,
    @Column(nullable = false)
    var pricePerHour: Int,
    @Column(nullable = false)
    var setupFee: Int,
    @Column(nullable = false)
    var dataFee: Int,
    @Column(nullable = false)
    var minPricePerUnit: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
