package com.nagarro.dataenterpriseplatform.main.AWS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.AmazonAthenaClient;
import com.amazonaws.services.athena.model.ListDataCatalogsRequest;


@Configuration
public class AwsAthenaConfiguration {
	
	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.sessionToken}")
	private String awsSessionToken;

	@Value("${aws.glue.glewDbName}")
	private String awsGlewDbName;
	
	@Bean
	public AmazonAthena createAwsAthenaClient() {
		
		final BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey,
				awsSessionToken);
		final AmazonAthena awsAthenaClient = AmazonAthenaClient.builder().withRegion("us-east-1")
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		return awsAthenaClient;
	}

}
