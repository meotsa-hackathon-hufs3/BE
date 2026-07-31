package com.meotsa.global.exception

import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val message: String
}
