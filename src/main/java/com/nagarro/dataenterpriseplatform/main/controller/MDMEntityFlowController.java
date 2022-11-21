package com.nagarro.dataenterpriseplatform.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsStepFunctionConfiguration;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

@RestController
@RequestMapping("/api/mdm/flow")
public class MDMEntityFlowController {

	@Value("${aws.accessKey}")
	private String dynamodbAccessKey;

	@Value("${aws.secretKey}")
	private String dynamodbSecretKey;

	@Value("${aws.sessionToken}")
	private String dynamodbSessionToken;

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.mdmArn}")
	private String awsMdmARN;

	@Autowired
	private AmazonS3 amazonS3;

	// @Autowired
	// private AWSStepFunctions sfnClient;

	@PostMapping(value = "/store-json/{entityName}")
	public ResponseEntity<?> storeJson(@RequestBody String form, @PathVariable("entityName") String entityName,

			HttpServletRequest request, HttpServletResponse response) {
		try {

			String bucketName = awsBucketName;
			String fileName = "mdm/" + entityName + "/" + entityName + ".json";

			PutObjectResult result = amazonS3.putObject(bucketName, fileName, form.replaceAll("[^\\x00-\\x7F]", ""));

			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
		}
	}

	/*
	 * API for executing step function for MDM flow
	 */

	@GetMapping(value = "/execute-step-function/{entity_name}")
	public ResponseEntity<?> runMdmFlow(@PathVariable("entity_name") String entity_name) {
		JSONObject input = new JSONObject();
		input.put("entity_name", entity_name);
		input.put("bucket_name", awsBucketName);

		StartExecutionRequest executionRequest = new StartExecutionRequest()
				.withStateMachineArn(awsMdmARN)
				.withInput(input.toString());

		AwsStepFunctionConfiguration.awsRegion = "us-east-1";
		AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
		AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
		AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;

		StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
				.startExecution(executionRequest);

		return ResponseEntity.ok().build();
	}

}
