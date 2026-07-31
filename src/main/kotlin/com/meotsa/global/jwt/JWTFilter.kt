package com.meotsa.global.jwt

import com.meotsa.global.security.CustomUserDetails
import com.meotsa.user.entity.Role
import com.meotsa.user.entity.User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter(
    private val jwtTokenProvider: JWTTokenProvider,
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization")

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorization.split(" ")[1]

        if (jwtTokenProvider.isExpired(token)) {
            filterChain.doFilter(request, response)
            return
        }

        if (jwtTokenProvider.getCategory(token) != "access") {
            filterChain.doFilter(request, response)
            return
        }

        val user = User(
            username = jwtTokenProvider.getUsername(token),
            password = "temppassword",
            role = Role.of(jwtTokenProvider.getRole(token)),
        )
        val customUserDetails = CustomUserDetails(user)
        val authToken = UsernamePasswordAuthenticationToken(
            customUserDetails,
            null,
            customUserDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authToken

        filterChain.doFilter(request, response)
    }
}