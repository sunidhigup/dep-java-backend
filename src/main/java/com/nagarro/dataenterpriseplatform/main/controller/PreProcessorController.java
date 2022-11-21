package com.nagarro.dataenterpriseplatform.main.controller;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.nagarro.dataenterpriseplatform.main.AWS.multitenant.AWSLamdaConfig;
import com.nagarro.dataenterpriseplatform.main.AWS.multitenant.AWSS3Configuration;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationStatusDbService;
import com.nagarro.dataenterpriseplatform.main.dto.PreProcessorResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.PreProcessorService;
import com.nagarro.dataenterpriseplatform.main.utils.Utility;

@RestController
@RequestMapping(value = "/api/pre-processor")
@CrossOrigin(value = "*")
public class PreProcessorController {

	@Value("${aws.accessKey}")
	private String dynamodbAccessKey;

	@Value("${aws.secretKey}")
	private String dynamodbSecretKey;

	@Value("${aws.sessionToken}")
	private String dynamodbSessionToken;

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private DepPreProcessorFileClassificationStatusDbService depPreProcessorFileClassificationStatusDbService;

	@Autowired
	private DepClientDbService depClientDbService;

	@Value("${aws.preProcessLambdaFunctn}")
	private String lambdaFuncName;

	@Value("${region_name}")
	private String region_name;

	private String client_name;

	private String batch_name;

	private String table_name;

	@Value("${aws.zipSteFunctnArn}")
	private String zipStateMachineARN;

	@Value("${aws.pdfSteFunctnArn}")
	private String pdfStateMachineARN;

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public String getBatch_name() {
		return batch_name;
	}

	public void setBatch_name(String batch_name) {
		this.batch_name = batch_name;
	}

	@Autowired
	private AWSLambda lambdaClient;

	@Autowired
	private DepPreProcessorFileClassificationDbService depPreProcessorFileClassificationDbService;

	@Autowired
	private PreProcessorService preProcessorService;

	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();

	@GetMapping("/execute-preprocess/")
	public ResponseEntity<?> executePreProcessor(@RequestParam String id) {
		try {
			String status = this.depPreProcessorFileClassificationStatusDbService.getPreProcessorStatus(id);
			if (status.equalsIgnoreCase("Failed") || status.toLowerCase().contains("error")) {
				final InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaFuncName);
				lambdaClient.invoke(invokeRequest);
			}
			return ResponseEntity.ok().build();
		} catch (ServiceException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@PostMapping(value = "/add-preprocess")
	public ResponseEntity<?> addPreProcessor(@RequestBody DepPreProcessorFileClassificationEntity preprocess) {
		try {
			boolean exist = depPreProcessorFileClassificationDbService.createPreprocessor(preprocess);
			if (exist == true) {
				return ResponseEntity.status(HttpStatus.CREATED).build();
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}

	}

	@GetMapping(value = "/get-all-preprocessor")
	public ResponseEntity<?> getAllPreProcessor() {
		try {
			List<DepPreProcessorFileClassificationEntity> preProcessList = depPreProcessorFileClassificationDbService
					.getAllPreprocessor();
			if (preProcessList == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(preProcessList);
			}
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}

	}

	@GetMapping(value = "/get-preprocessor/{client_name}/{batch_name}")
	public ResponseEntity<?> getClientBatchPreProceesor(@PathVariable("client_name") String client_name,
			@PathVariable("batch_name") String batch_name) {
		try {
			List<DepPreProcessorFileClassificationEntity> preprocessorList = depPreProcessorFileClassificationDbService
					.getPreProcessorClientBatch(client_name, batch_name);
			if (preprocessorList == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(preprocessorList);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

	@GetMapping(value = "/preprocessor-status/{client_name}/{batch_name}/{table_name}")
	public ResponseEntity<?> getClientBatchPreProceesor(@PathVariable("client_name") String client_name,
			@PathVariable("batch_name") String batch_name, @PathVariable("table_name") String table_name) {
		try {
			String id = client_name + "_" + batch_name + "_" + table_name;
			String status = this.depPreProcessorFileClassificationStatusDbService.getPreProcessorStatus(id);
			return ResponseEntity.status(HttpStatus.OK).body(status);
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

	@GetMapping(value = "/fetch-preprocess/{prefix}")
	public ResponseEntity<?> fetchPreProcessorId(@PathVariable("prefix") String prefix) {
		try {
			List<JSONObject> fetchedId = this.depPreProcessorFileClassificationStatusDbService.getPreprocessor(prefix);

			if (fetchedId == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(fetchedId.get(0).toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping(value = "/execute-preprocess")
	public ResponseEntity<?> executePreProcessor(@RequestParam String bucket,
			@RequestParam String data_region,
			@RequestParam String key) {

		AWSLamdaConfig.awsAccessKey = dynamodbAccessKey;
		AWSLamdaConfig.awsSecretKey = dynamodbSecretKey;
		AWSLamdaConfig.sessionToken = dynamodbSessionToken;
		AWSLamdaConfig.awsRegion = data_region;

		InvokeResult invokeResult = null;
		try {
			InvokeRequest invokeRequest = new InvokeRequest().withFunctionName(lambdaFuncName)
					.withPayload("{\r\n" + "	\"Records\": [{\r\n" + "		\"s3\": {\r\n"
							+ "			\"bucket\": {\r\n" + "				\"name\": \"" + bucket + "\"\r\n"
							+ "			},\r\n" + "			\"object\": {\r\n" + "				\"key\": \"" + key
							+ "\"\r\n" + "			}\r\n" + "		}\r\n" + "	}]\r\n" + "}");
			invokeRequest.setInvocationType(InvocationType.Event);
			invokeResult = lambdaClient.invoke(invokeRequest);
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

		}
		// InvokeResult invokeResult = null;
		// try {
		// InvokeRequest invokeRequest = new
		// InvokeRequest().withFunctionName(lambdaFuncName)
		// .withPayload("{\r\n" + " \"Records\": [{\r\n" + " \"s3\": {\r\n"
		// + " \"bucket\": {\r\n" + " \"name\": \"" + bucket + "\"\r\n"
		// + " },\r\n" + " \"object\": {\r\n" + " \"key\": \"" + key
		// + "\"\r\n" + " }\r\n" + " }\r\n" + " }]\r\n" + "}");
		// invokeRequest.setInvocationType(InvocationType.Event);
		// invokeResult = lambdaClient.invoke(invokeRequest);
		// return ResponseEntity.ok().build();

		// } catch (Exception e) {
		// return
		// ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

		// }

	}

	/*
	 * API to fetch all the s3 folder of given prefix
	 */

	@GetMapping(value = "/get-all-s3-data-list/{bucket_name}/{data_region}/{client_name}/{batch_name}/{table_name}")
	public ResponseEntity<?> getS3List(@PathVariable("bucket_name") String bucket_name,
			@PathVariable("data_region") String data_region,
			@PathVariable("client_name") String client_name,
			@PathVariable("batch_name") String batch_name,
			@PathVariable("table_name") String table_name) {
		try {
			System.out.println(bucket_name + data_region + client_name + batch_name + table_name);
			// System.out.println(user_id + client_name + batch_name + table_name);
			// DepClientEntity getClient =
			// depClientDbService.getDepClientByClientNameAndUserId(client_name, user_id);
			// List<JSONObject> list = Utility.getAllS3List(awsBucketName, amazonS3,
			// client_name, batch_name, table_name,
			// "preprocessor");

			AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
			AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
			AWSS3Configuration.sessionToken = dynamodbSessionToken;
			AWSS3Configuration.awsRegion = data_region;
			List<JSONObject> list = Utility.getAllS3List(bucket_name,
					AWSS3Configuration.amazonS3Config(),
					client_name,
					batch_name,
					table_name,
					"preprocessor");

			return ResponseEntity.status(HttpStatus.OK).body(list.toString());
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

	/*
	 * API to fetch all the s3 files of particular folder of given prefix
	 */

	@GetMapping(value = "/get-each-s3-data-list/{bucket_name}/{data_region}")
	public ResponseEntity<?> getS3ParticularListData(@RequestParam String prefix,
			@PathVariable("bucket_name") String bucket_name,
			@PathVariable("data_region") String data_region) {
		try {
			AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
			AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
			AWSS3Configuration.sessionToken = dynamodbSessionToken;
			AWSS3Configuration.awsRegion = data_region;
			List<JSONObject> list = Utility.getEachS3List(bucket_name, AWSS3Configuration.amazonS3Config(),
					prefix);

			return ResponseEntity.status(HttpStatus.OK).body(list.toString());
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

	/*
	 * API to disable and enable preProcessor job
	 */

	@PutMapping(value = "/disableEnablePreProcessorJob")
	public ResponseEntity<?> disableEnableJob(@RequestBody DepPreProcessorFileClassificationEntity data) {
		Boolean job = depPreProcessorFileClassificationDbService.DisableEnableJob(data);
		if (job) {
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping(value = "/update-preprocess")
	public ResponseEntity<?> updateManagementEmployeeDetail(
			@RequestBody DepPreProcessorFileClassificationEntity preprocess) {
		try {
			depPreProcessorFileClassificationDbService.updatePreprocess(preprocess);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(value = "/get-all-preprocessor-jobs-with-status/{client_name}/{batch_name}")
	public ResponseEntity<?> getAllPreprocessorJobsWithStatus(@PathVariable("client_name") String client_name,
			@PathVariable("batch_name") String batch_name) {
		try {
			final List<PreProcessorResponseDto> preprocessorList = preProcessorService
					.getAllPreprocessorJobsWithStatus(client_name, batch_name);

			if (preprocessorList == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(preprocessorList);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}
}
