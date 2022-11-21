package com.nagarro.dataenterpriseplatform.main.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;
import com.nagarro.dataenterpriseplatform.main.AWS.multitenant.AWSS3Configuration;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderFormEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobInputEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepPredictiveModelsEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderFormDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobInputDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPredictiveModelsDbService;
import com.nagarro.dataenterpriseplatform.main.dto.ReadFormDto;
import com.nagarro.dataenterpriseplatform.main.dto.ReadNodesDto;
import com.nagarro.dataenterpriseplatform.main.dto.TableInputDto;
import com.nagarro.dataenterpriseplatform.main.dto.UdfNamesDto;
import com.nagarro.dataenterpriseplatform.main.utils.Utility;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;

/*
 * Controller class for performing operation on flow builder form data
 * */

@RestController
@RequestMapping("/api/flow-builder-form")
@CrossOrigin(origins = "*")
public class FlowBuilderFormController {

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.flowBuilderJsonLocation}")
	private String awsFlowBuilderJsonLocation;

	@Value("${aws.accessKey}")
	private String dynamodbAccessKey;

	@Value("${aws.secretKey}")
	private String dynamodbSecretKey;

	@Value("${aws.sessionToken}")
	private String dynamodbSessionToken;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private DepFlowBuilderJobInputDbService depFlowBuilderJobInputDbService;

	@Autowired
	private DepFlowBuilderFormDbService depFlowBuilderFormDbService;

	@Autowired
	private DepPredictiveModelsDbService depPredictiveModelsDbService;

	@Autowired
	DepClientDbService depClientDbService;

	/*
	 * API for fetching udf json from s3
	 */

	@PostMapping("/get-udf-names/{client_name/{userId}")
	public ResponseEntity<?> getTableRules(@RequestBody TableInputDto tab, @PathVariable String client_name,
			@PathVariable String userId, HttpServletRequest request, HttpServletResponse response) {

		try {
			DepClientEntity depClient = this.depFlowBuilderFormDbService.getClientByClientNameAndUserId(client_name,
					userId);

			String bucketName = depClient.getBucket_name();
			S3Object o = amazonS3.getObject(bucketName, tab.getPath());
			S3ObjectInputStream s3is = o.getObjectContent();

			StringBuilder sb = new StringBuilder();
			BufferedReader reader = null;
			reader = new BufferedReader(new InputStreamReader(s3is));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			Gson gson = new Gson();
			UdfNamesDto json = gson.fromJson(sb.toString(), UdfNamesDto.class);

			return ResponseEntity.status(HttpStatus.OK).body(json);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	/*
	 * API to store flow builder form data in json form at S3 location
	 */

	@PostMapping(value = "/convert-to-json/{client}/{batch}/{job}/{trackingId}/{timestamptype}/{bucket_name}/{data_region}")
	public ResponseEntity<?> convertData(@RequestBody String form,
			@PathVariable("client") String client,
			@PathVariable("batch") String batch,
			@PathVariable("job") String job,
			@PathVariable("trackingId") String trackingId,
			@PathVariable("timestamptype") String timestamptype,
			@PathVariable("bucket_name") String bucket_name,
			@PathVariable("data_region") String data_region,
			HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// DepClientEntity depClient =
			// this.depFlowBuilderFormDbService.getClientByClientNameAndUserId(client,
			// userId);

			// String bucketName = awsBucketName;

			AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
			AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
			AWSS3Configuration.sessionToken = dynamodbSessionToken;
			AWSS3Configuration.awsRegion = data_region;

			String fileName = "";
			if (trackingId.equals("snowflake-garbageTID")) {
				fileName = client + "/" + batch + "/" + job + "/" + awsFlowBuilderJsonLocation + "/" + job + ".json";
			} else if (trackingId.equals("glue-garbageTID")) {
				fileName = client + "/" + batch + "/" + job + "/" + awsFlowBuilderJsonLocation + "/" + trackingId + "/"
						+ job + ".json";
			} else {
				fileName = client + "/" + batch + "/" + job + "/" + awsFlowBuilderJsonLocation + "/" + trackingId + "/"
						+ job + ".json";
			}

			PutObjectResult result = AWSS3Configuration.amazonS3Config()
					.putObject(bucket_name, fileName, form.replaceAll("[^\\x00-\\x7F]", ""));

			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	/*
	 * API to fetch flow builder form data
	 */

	@GetMapping(value = "/get-form-data/{client_id}/{batch_id}/{job}")
	public ResponseEntity<DepFlowBuilderFormEntity> fetchJobForm(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("job") String job, HttpServletRequest request,
			HttpServletResponse response) {
		DepFlowBuilderFormEntity list = this.depFlowBuilderFormDbService.getFormData(client_id, batch_id, job);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(list);
	}

	/*
	 * API to store flow builder form data
	 */

	@PostMapping(value = "/add-form-data")
	public ResponseEntity<DepFlowBuilderFormEntity> addFormData(@RequestBody DepFlowBuilderFormEntity form,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			this.depFlowBuilderFormDbService.createNewFormData(form);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API delete flow builder form data
	 */

	@DeleteMapping(value = "/delete-form/{client_id}/{batch_id}/{jobname}")
	public ResponseEntity<?> deleteForm(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("jobname") String job, HttpServletRequest request,
			HttpServletResponse response) {
		this.depFlowBuilderFormDbService.deleteFormData(client_id, batch_id, job);
		return ResponseEntity.ok().build();
	}

	/*
	 * API to fetch the job input data
	 */

	@GetMapping(value = "/get-job-input/{client_id}/{batch_id}/{job}")
	public ResponseEntity<DepFlowBuilderJobInputEntity> fetchJobInputForm(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("job") String job, HttpServletRequest request,
			HttpServletResponse response) {
		DepFlowBuilderJobInputEntity list = this.depFlowBuilderJobInputDbService.getJobInput(client_id, batch_id, job);
		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(list);
	}

	/*
	 * API to add job input data
	 */

	@PostMapping(value = "/add-job-input/{client_id}/{batch_id}/{batch}/{job}")
	public ResponseEntity<?> createReadInput(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("job") String job,
			@PathVariable("batch") String batch, @RequestBody ReadNodesDto readForms, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			this.depFlowBuilderJobInputDbService.insertJobInput(client_id, batch_id, job, batch, readForms);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to delete job input data
	 */

	@DeleteMapping(value = "/delete-job-input/{client_id}/{batch_id}/{jobname}")
	public ResponseEntity<?> deleteInput(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("jobname") String job, HttpServletRequest request,
			HttpServletResponse response) {
		this.depFlowBuilderJobInputDbService.deleteJobInput(client_id, batch_id, job);
		return ResponseEntity.ok().build();
	}

	/*
	 * API to delete json file of flow builder data from S3
	 */

	@DeleteMapping(value = "/delete-json/{batch}/{jobname}")
	public ResponseEntity<ReadFormDto> deleteJson(@PathVariable("batch") String batch,
			@PathVariable("jobname") String job, HttpServletRequest request, HttpServletResponse response) {
		this.depFlowBuilderJobInputDbService.deleteJsonFromS3(batch, job);
		return ResponseEntity.ok().build();
	}

	/*
	 * API to fetch all segments of predictive models
	 */

	@GetMapping(value = "/customer-360/getAllSegments")
	public ResponseEntity<?> fetchAllSegments(HttpServletRequest request, HttpServletResponse response) {
		List<DepPredictiveModelsEntity> list = this.depPredictiveModelsDbService.getAllSgements();

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(list);
	}

	/*
	 * API to fetch all the s3 folder of given prefix
	 */

	@GetMapping(value = "/get-all-s3-data-list/{client_name}/{batch_name}/{table_name}/{TimestampType}/{client_id}")
	public ResponseEntity<?> getS3List(@PathVariable("client_name") String client_name,
			@PathVariable("batch_name") String batch_name,
			@PathVariable("table_name") String table_name,
			@PathVariable("TimestampType") String TimestampType,
			@PathVariable("client_id") String client_id) {
		try {
			DepClientEntity getClient = depClientDbService.getClientById(client_id);
			AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
			AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
			AWSS3Configuration.sessionToken = dynamodbSessionToken;
			AWSS3Configuration.awsRegion = getClient.getDataRegionEntity().getData_region_code();

			List<JSONObject> list = Utility.getAllS3ListFlowBuilder(getClient.getDataRegionEntity().getBucket_name(),
					AWSS3Configuration.amazonS3Config(), client_name, batch_name,
					table_name, TimestampType);

			return ResponseEntity.status(HttpStatus.OK).body(list.toString());
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

	/*
	 * API to fetch all the s3 files of particular folder of given prefix
	 */

	@GetMapping(value = "/get-each-s3-data-list/{client_id}")
	public ResponseEntity<?> getS3ParticularListData(@RequestParam String prefix,
			@PathVariable("client_id") String client_id) {
		try {
			DepClientEntity getClient = depClientDbService.getClientById(client_id);
			AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
			AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
			AWSS3Configuration.sessionToken = dynamodbSessionToken;
			AWSS3Configuration.awsRegion = getClient.getDataRegionEntity().getData_region_code();
			List<JSONObject> list = Utility.getEachS3List(getClient.getDataRegionEntity().getBucket_name(),
					AWSS3Configuration.amazonS3Config(), prefix);

			return ResponseEntity.status(HttpStatus.OK).body(list.toString());
		} catch (Exception exc) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
		}
	}

}
