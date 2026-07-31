package com.meotsa.global.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.global.security.CustomUserDetails
import com.meotsa.global.security.RestAuthenticationEntryPoint
import com.meotsa.user.entity.Role
import com.meotsa.user.entity.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter(
    private val jwtTokenProvider: JWTTokenProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorization = request.getHeader("Authorization")

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorization.split(" ")[1]

        try {
            val info = jwtTokenProvider.parse(token)
            if (info.category != "access") {
                RestAuthenticationEntryPoint.write(response, objectMapper, "유효하지 않은 토큰입니다")
                return
            }

            val user =
                User(
                    username = info.username,
                    password = "temppassword",
                    role = Role.of(info.role),
                )
            val customUserDetails = CustomUserDetails(user)
            val authToken =
                UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    customUserDetails.authorities,
                )

            SecurityContextHolder.getContext().authentication = authToken
        } catch (e: ExpiredJwtException) {
            RestAuthenticationEntryPoint.write(response, objectMapper, "만료된 토큰입니다")
            return
        } catch (e: JwtException) {
            RestAuthenticationEntryPoint.write(response, objectMapper, "유효하지 않은 토큰입니다")
            return
        } catch (e: IllegalArgumentException) {
            RestAuthenticationEntryPoint.write(response, objectMapper, "유효하지 않은 토큰입니다")
            return
        }

        filterChain.doFilter(request, response)
    }
}
