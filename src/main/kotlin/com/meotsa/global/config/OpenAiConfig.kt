package com.meotsa.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class OpenAiConfig(
    private val openAiProperties: OpenAiProperties,
) {
    @Bean
    fun openAiRestClient(): RestClient {
        val requestFactory =
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(Duration.ofSeconds(10))
                setReadTimeout(openAiProperties.timeout)
            }

        return RestClient
            .builder()
            .baseUrl(openAiProperties.baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${openAiProperties.apiKey}")
            .requestFactory(requestFactory)
            .build()
    }
}
