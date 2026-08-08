package com.meotsa.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class SqsConfig(
    private val awsProperties: AwsProperties,
) {
    @Bean
    fun sqsClient(): SqsClient {
        val credentials =
            AwsBasicCredentials.create(
                awsProperties.credentials.accessKey,
                awsProperties.credentials.secretKey,
            )
        return SqsClient
            .builder()
            .region(Region.of(awsProperties.region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}
