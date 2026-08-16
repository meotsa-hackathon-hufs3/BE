package com.meotsa.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "openai")
data class OpenAiProperties(
    val apiKey: String,
    val baseUrl: String,
    val model: String,
    val size: String,
    val quality: String,
    val timeout: Duration,
)
