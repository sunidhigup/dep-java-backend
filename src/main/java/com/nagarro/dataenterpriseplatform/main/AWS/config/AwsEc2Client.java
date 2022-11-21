package com.nagarro.dataenterpriseplatform.main.AWS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;


@Configuration
public class AwsEc2Client {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.sessionToken}")
	private String awsSessionToken;
	
    @Bean
    public AmazonEC2 getAmazonSec2Client() {
    	final BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey,
				awsSessionToken);
    	final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    	return ec2;
    }
}
