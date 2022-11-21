package com.nagarro.dataenterpriseplatform.main.AWS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

@Configuration
public class AwsSecretsmanagerConfiguration {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.sessionToken}")
	private String awsSessionToken;

	@Bean
	public AWSSecretsManager aWSSecretsManager() {
		final BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey,
				awsSessionToken);
		try {
			final AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(awsRegion)
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			return client;
		}
		catch(Exception e) {
			System.out.println("res"+e);
			throw e;
		}
		
	}
}
