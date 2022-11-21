package com.nagarro.dataenterpriseplatform.main.AWS.multitenant;

import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSS3Configuration {

    public static String awsRegion;

    public static String awsAccessKey;

    public static String awsSecretKey;

    public static String sessionToken;

    private static AmazonS3 amazonS3Connection;

    public static AmazonS3 amazonS3Config() {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey, sessionToken);

        amazonS3Connection = AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        return amazonS3Connection;
    }

}
