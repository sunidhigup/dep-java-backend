package com.nagarro.dataenterpriseplatform.main.AWS.multitenant;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSLamdaConfig {

    public static String awsRegion;

    public static String awsAccessKey;

    public static String awsSecretKey;

    public static String sessionToken;

    private static AWSLambda awsLambdaConnection;

    public static AWSLambda awsLambdaConfig() {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey, sessionToken);

        awsLambdaConnection = AWSLambdaClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        return awsLambdaConnection;
    }
}
