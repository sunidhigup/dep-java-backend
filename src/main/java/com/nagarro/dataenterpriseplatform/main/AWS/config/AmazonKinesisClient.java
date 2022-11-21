package com.nagarro.dataenterpriseplatform.main.AWS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;

@Configuration
public class AmazonKinesisClient {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.sessionToken}")
	private String sessionToken;

	@Bean
	public AmazonKinesis buildAmazonKinesis() {
		BasicSessionCredentials awsCreds = new BasicSessionCredentials(accessKey, secretKey, sessionToken);
		AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();

		clientBuilder.setRegion(awsRegion);
		clientBuilder.withCredentials(new AWSStaticCredentialsProvider(awsCreds));

		AmazonKinesis akClient = clientBuilder.build();

		return akClient;

	}
}
