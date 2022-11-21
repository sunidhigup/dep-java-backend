package com.nagarro.dataenterpriseplatform.main.AWS.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSLambdaFnConfiguration {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accessKey}")
    private String dynamodbAccessKey;

    @Value("${aws.secretKey}")
    private String dynamodbSecretKey;

    @Value("${aws.sessionToken}")
    private String dynamodbSessionToken;

    @Bean
    public AWSLambda awsLambda() {

        BasicSessionCredentials awsCreds = new BasicSessionCredentials(dynamodbAccessKey, dynamodbSecretKey,
                dynamodbSessionToken);
        // BasicAWSCredentials awsCreds = new BasicAWSCredentials(dynamodbAccessKey,
        // dynamodbSecretKey);
        return AWSLambdaClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
