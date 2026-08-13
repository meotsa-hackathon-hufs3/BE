package com.meotsa.creation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "3D_file")
class File3D(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file", nullable = false)
    val imageFile: File,
    @Column(name = "s3_key", unique = true, nullable = false)
    val s3Key: String,
    @Column(nullable = false)
    val contentType: String,
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
