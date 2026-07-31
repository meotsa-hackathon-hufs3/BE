package com.meotsa.user.repository

import com.meotsa.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?
}
