package com.meotsa.auth.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RegisterRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    val username: String,
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Pattern(
        regexp = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d\s]).{8,}$""",
        message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 각각 1자 이상 포함해야 합니다",
    )
    val password: String,
)
