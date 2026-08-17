package com.meotsa.creation.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ModelGeometry(
    @Column(name = "volume_mm3")
    val volumeMm3: Double,
    @Column(name = "bbox_x")
    val bboxX: Double,
    @Column(name = "bbox_y")
    val bboxY: Double,
    @Column(name = "bbox_z")
    val bboxZ: Double,
)
