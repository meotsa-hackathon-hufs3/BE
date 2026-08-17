package com.meotsa.creation.entity

import jakarta.persistence.Column
import jakarta.persistence.Embedded
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
import java.time.LocalDateTime

@Entity
@Table(name = "job")
class Job(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creation_id", nullable = false)
    val creation: Creation,
    @Embedded
    val option: ModelOption,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: JobStatus = JobStatus.PENDING

    @Column(name = "model_key")
    var modelKey: String? = null

    @Embedded
    var geometry: ModelGeometry? = null

    @Column
    var structureCheck: Boolean? = null

    @Column
    var widthCheck: Boolean? = null

    @Column
    var expectedFee: Int? = null

    @Column
    var error: String? = null

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    fun complete(
        modelKey: String?,
        geometry: ModelGeometry,
        structureCheck: Boolean?,
        widthCheck: Boolean?,
        expectedFee: Int?,
    ) {
        this.status = JobStatus.COMPLETED
        this.modelKey = modelKey
        this.geometry = geometry
        this.structureCheck = structureCheck
        this.widthCheck = widthCheck
        this.expectedFee = expectedFee
    }

    fun fail(error: String?) {
        this.status = JobStatus.FAILED
        this.error = error
    }
}
