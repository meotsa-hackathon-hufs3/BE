package com.meotsa.file.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "file")
class File(
    @Column(name = "s3_key", nullable = false, unique = true)
    val s3Key: String,
    @Column(nullable = false)
    val originalFileName: String,
    @Column(nullable = false)
    val contentType: String,
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
