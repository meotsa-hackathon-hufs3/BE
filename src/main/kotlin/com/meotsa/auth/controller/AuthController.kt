package com.meotsa.auth.controller

import com.meotsa.auth.dto.LoginRequest
import com.meotsa.auth.dto.RegisterRequest
import com.meotsa.auth.dto.RegisterResponse
import com.meotsa.auth.dto.ReissueRequest
import com.meotsa.auth.dto.TokenResponse
import com.meotsa.auth.service.AuthService
import com.meotsa.global.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<RegisterResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.register(registerRequest))

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<TokenResponse> = ResponseEntity.ok(authService.login(loginRequest))

    @PostMapping("/reissue")
    fun reissue(
        @Valid @RequestBody reissueRequest: ReissueRequest,
    ): ResponseEntity<TokenResponse> = ResponseEntity.ok(authService.reissue(reissueRequest))

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<Void> {
        authService.logout(userDetails.username)
        return ResponseEntity.noContent().build()
    }
}
