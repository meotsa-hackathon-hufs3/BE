package com.meotsa.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.global.jwt.JWTFilter
import com.meotsa.global.jwt.JWTTokenProvider
import com.meotsa.global.security.RestAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtTokenProvider: JWTTokenProvider,
    private val corsConfigurationSource: CorsConfigurationSource,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
                    .permitAll()
                    .requestMatchers("/actuator/health")
                    .permitAll()
//                    .requestMatchers("/admin").hasRole("ADMIN")
                    .requestMatchers("/auth/logout")
                    .authenticated()
                    .anyRequest()
                    .permitAll() // 나머지는 개발용으로 열어둠
            }.addFilterBefore(
                JWTFilter(jwtTokenProvider, objectMapper),
                UsernamePasswordAuthenticationFilter::class.java,
            ).exceptionHandling { it.authenticationEntryPoint(restAuthenticationEntryPoint) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .cors { it.configurationSource(corsConfigurationSource) }

        return http.build()
    }

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager = configuration.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
