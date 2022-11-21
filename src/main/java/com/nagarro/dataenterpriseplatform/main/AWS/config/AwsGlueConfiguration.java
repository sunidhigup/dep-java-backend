package com.nagarro.dataenterpriseplatform.main.AWS.config;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.glue.AWSGlue;
import com.amazonaws.services.glue.AWSGlueClient;
import com.amazonaws.services.glue.model.Crawler;
import com.amazonaws.services.glue.model.CrawlerTargets;
import com.amazonaws.services.glue.model.CreateCrawlerRequest;
import com.amazonaws.services.glue.model.CreateDatabaseRequest;
import com.amazonaws.services.glue.model.CreateDatabaseResult;
import com.amazonaws.services.glue.model.DatabaseInput;
import com.amazonaws.services.glue.model.GetCrawlerRequest;
import com.amazonaws.services.glue.model.GetDatabaseRequest;
import com.amazonaws.services.glue.model.S3Target;
import com.amazonaws.services.glue.model.StartCrawlerRequest;
import com.amazonaws.services.glue.model.StartCrawlerResult;

@Configuration
public class AwsGlueConfiguration {

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
	public AWSGlue createGlueClient() {
		final BasicSessionCredentials awsCreds = new BasicSessionCredentials(awsAccessKey, awsSecretKey,
				awsSessionToken);
		AWSGlue glueClient = AWSGlueClient.builder().withRegion("us-east-1")
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();		
		return glueClient;
	}

	public StartCrawlerResult createGlueCrawler(AWSGlue glueClient, String iam, String s3Path,
			String dbName, String crawlerName) {
		StartCrawlerResult res = null;
		Crawler response = null;
		final GetCrawlerRequest getCrawlerRequest = new GetCrawlerRequest().withName(crawlerName);
		try {
			response = glueClient.getCrawler(getCrawlerRequest).getCrawler();
		} catch (Exception e) {
			System.out.println("error"+e);
		} finally {
			if (response == null) {
				final S3Target s3Target = new S3Target().withPath(s3Path);
				final List<S3Target> targetList = new ArrayList<>();
				targetList.add(s3Target);
				final CrawlerTargets targets = new CrawlerTargets().withS3Targets(targetList);
				final CreateCrawlerRequest crawlerRequest = new CreateCrawlerRequest().withDatabaseName(dbName)
						.withName(crawlerName).withTargets(targets).withRole(iam);
				crawlerRequest.setConfiguration(
						"{\"Version\": 1.0,\"CrawlerOutput\": {\"Partitions\": { \"AddOrUpdateBehavior\": \"InheritFromTable\" }}}");
				glueClient.createCrawler(crawlerRequest);
			}
		}
		final StartCrawlerRequest startCrawlerRequest = new StartCrawlerRequest().withName(crawlerName);
		res = glueClient.startCrawler(startCrawlerRequest);
		return res;
	}
	
	
	public CreateDatabaseResult createGlueDatabase(String dbName) {
		CreateDatabaseResult createDatabaseResult =  new CreateDatabaseResult();
		final GetDatabaseRequest getDatabase = new GetDatabaseRequest().withName(dbName);
		if (getDatabase.getName() == null && getDatabase.getName().toString().isEmpty()) {
			final DatabaseInput input = new DatabaseInput();
			input.setName(dbName);
			final CreateDatabaseRequest databaseRequest = new CreateDatabaseRequest().withDatabaseInput(input);
			return createGlueClient().createDatabase(databaseRequest);
		}
		return createDatabaseResult;
	}
}