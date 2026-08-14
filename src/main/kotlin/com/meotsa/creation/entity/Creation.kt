package com.meotsa.creation.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "creation")
class Creation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "original_image_key")
    var originalImageKey: String? = null

    @Column(name = "stylized_image_key")
    var stylizedImageKey: String? = null

    @Column(name = "model_key")
    var modelKey: String? = null

    @Embedded
    var option: ModelOption? = null

    @Column
    var expectedFee: Int? = null

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun stylize(
        originalImageKey: String,
        stylizedImageKey: String,
    ) {
        this.originalImageKey = originalImageKey
        this.stylizedImageKey = stylizedImageKey
    }

    fun reStylize(stylizedImageKey: String) {
        this.stylizedImageKey = stylizedImageKey
    }

    fun completeModel(
        modelKey: String,
        option: ModelOption,
        expectedFee: Int?,
    ) {
        this.modelKey = modelKey
        this.option = option
        this.expectedFee = expectedFee
    }
}
