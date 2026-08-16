package com.meotsa.printshop.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "print_shop")
class PrintShop(
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val tag: String,
    @Column(nullable = false)
    val minQuantity: Int,
    @Column(nullable = false)
    val shippingCost: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
