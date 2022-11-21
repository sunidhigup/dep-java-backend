package com.nagarro.dataenterpriseplatform.main.AWS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;

@Configuration
public class AmazonEmrClient {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.sessionToken}")
	private String sessionToken;

	@Bean
	public AmazonElasticMapReduce buildAmazonEmr() {
		BasicSessionCredentials awsCreds = new BasicSessionCredentials(accessKey, secretKey, sessionToken);

		return AmazonElasticMapReduceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(awsRegion).build();
	}

}
