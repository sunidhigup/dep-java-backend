package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;
import java.util.Optional;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.*;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsStepFunctionConfiguration;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;

import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowbuilderJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.utils.ValidateAwsCognitoJwtToken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.nagarro.dataenterpriseplatform.main.service.BatchService;

/**
 * Controller class for performing operations on batch and implementing step
 * function
 */

@RestController
@RequestMapping("/api/batch")
@CrossOrigin(origins = "*")
public class BatchController {

	@Value("${aws.accessKey}")
	private String dynamodbAccessKey;

	@Value("${aws.secretKey}")
	private String dynamodbSecretKey;

	@Value("${aws.sessionToken}")
	private String dynamodbSessionToken;

	@Value("${profile_env}")
	private String profile_env;

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.stateMachineARN}")
	private String awsStateMachineARN;

	@Value("${aws.region.euStateMachineARN}")
	private String euAWSStateMachineARN;

	@Autowired
	private DepFlowBuilderMetadataDbService depFlowBuilderMetadataDbService;

	@Autowired
	private DepFlowbuilderJobStatusDbService depFlowbuilderJobStatusDbService;

	// @Autowired
	// private AWSStepFunctions sfnClient;

	@Autowired
	private ValidateAwsCognitoJwtToken validateAwsCognitoJwtToken;

	@Autowired
	private BatchService batchService;

	@Autowired
	DepClientDbService depClientDbService;

	/*
	 * API for fetching all batches by client
	 */

	@GetMapping("/get-batch/{client_id}")
	public ResponseEntity<?> getBatch(@PathVariable("client_id") String client_id, HttpServletRequest request,
			HttpServletResponse response) {

		List<DepFlowBuilderMetadataEntity> list = this.depFlowBuilderMetadataDbService.getAllBatch(client_id);

		if (list.size() <= 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.of(Optional.of(list));
	}

	/*
	 * API to fetch batch by id
	 */

	@GetMapping("/get-batch-by-id/{batch_id}")
	public ResponseEntity<?> getBatchById(@PathVariable("batch_id") String batch_id, HttpServletRequest request,
			HttpServletResponse response) {

		DepFlowBuilderMetadataEntity list = this.depFlowBuilderMetadataDbService.getBatchbyId(batch_id);

		if (list.getBatch_id() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.of(Optional.of(list));
	}

	/*
	 * API for creating new batch
	 */

	@PostMapping("/add-batch")
	public ResponseEntity<DepFlowBuilderMetadataEntity> createBatch(
			@Valid @RequestBody DepFlowBuilderMetadataEntity batch, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean exist = this.depFlowBuilderMetadataDbService.addBatch(batch);
			if (exist) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	/*
	 * API for executing step function at batch level
	 */

	@GetMapping("/execute-step-function/{batch}/{batch_id}/{client_id}/{client_name}/{execution_id}")
	public ResponseEntity<?> executeBatch(@PathVariable("batch") String batch,
			@PathVariable("batch_id") String batch_id, @PathVariable("client_id") String client_id,
			@PathVariable("execution_id") String execution_id, @PathVariable("client_name") String client_name,
			HttpServletRequest request, HttpServletResponse response) {

		DepClientEntity getClient = depClientDbService.getClientById(client_id);

		JSONObject input = new JSONObject();
		input.put("rule_engine", false);
		input.put("batch_name", batch);
		input.put("client_id", client_id);
		input.put("batch_id", batch_id);
		input.put("execution_id", execution_id);
		input.put("client_name", client_name);
		input.put("profile_env_bucket", awsBucketName);
		input.put("profile_env_bucket", getClient.getDataRegionEntity().getBucket_name());
		input.put("profile_env", profile_env);

		String ARN = getClient.getDataRegionEntity().getData_region_arn();

		AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
		AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
		AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;
		AwsStepFunctionConfiguration.awsRegion = getClient.getDataRegionEntity().getData_region_code();

		StartExecutionRequest executionRequest = new StartExecutionRequest().withStateMachineArn(ARN)
				.withInput(input.toString());

		StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
				.startExecution(executionRequest);

		return ResponseEntity.ok().build();
	}

	/*
	 * API for getting current status of previously triggered step function
	 */

	@PostMapping("/get-status/{jobname}")
	public ResponseEntity<?> getJobStatus(@RequestBody DepFlowbuilderJobStatusEntity id,
			@PathVariable("jobname") String jobname, HttpServletRequest request, HttpServletResponse response) {
		try {
			final DepFlowbuilderJobStatusEntity list = batchService.getJobStatus(id);
			if (list == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@PutMapping("/update-batch-detail")
	public ResponseEntity<?> updateBatch(@RequestBody DepFlowBuilderMetadataEntity batch, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = this.depFlowBuilderMetadataDbService.UpdateBatch(batch);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@DeleteMapping("/delete-batch-detail")
	public ResponseEntity<?> deleteBatch(@RequestParam String batch_id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = this.depFlowBuilderMetadataDbService.DeleteBatch(batch_id);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@GetMapping("/get-approved-Batch")
	public ResponseEntity<?> getApprovedBatch(@RequestParam String client_id, HttpServletRequest request) {
		final List<DepFlowBuilderMetadataEntity> list = this.depFlowBuilderMetadataDbService
				.getApprovedBatch(client_id);
		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/get-all-batch")
	public ResponseEntity<?> getAllBatch(HttpServletRequest request, HttpServletResponse response) {
		return batchService.getAllBatch();
	}

}
