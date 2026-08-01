package com.meotsa.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    val username: String,
    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String,
)
