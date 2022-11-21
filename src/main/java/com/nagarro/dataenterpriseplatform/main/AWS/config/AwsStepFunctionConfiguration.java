package com.nagarro.dataenterpriseplatform.main.AWS.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsStepFunctionConfiguration {

    public static String awsRegion;

    public static String awsAccessKey;

    public static String awsSecretKey;

    public static String sessionToken;

    private static AWSStepFunctions stepFunctionsConnection;

    public static AWSStepFunctions awsStepFunctionsConfig() {
        // final BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey,
        // awsSecretKey);
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey, sessionToken);

        stepFunctionsConnection = AWSStepFunctionsClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        return stepFunctionsConnection;
    }

}
