package com.nagarro.dataenterpriseplatform.main.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobInputDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobNameDbService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.ListStateMachinesRequest;
import com.amazonaws.services.stepfunctions.model.ListStateMachinesResult;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.services.stepfunctions.model.StateMachineListItem;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsStepFunctionConfiguration;
import com.nagarro.dataenterpriseplatform.main.constants.AWSConstants;
import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowbuilderJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.dto.CopyJobDto;
import com.nagarro.dataenterpriseplatform.main.dto.FlowBuilderResponseDto;
import com.nagarro.dataenterpriseplatform.main.dto.JobInputExtractUpdateDto;
import com.nagarro.dataenterpriseplatform.main.service.ConfigureClientService;
import com.nagarro.dataenterpriseplatform.main.service.JobsService;
import com.nagarro.dataenterpriseplatform.main.service.SnowflakeService;
import com.nagarro.dataenterpriseplatform.main.service.impl.JobsServiceImpl;

/*
 * Controller class for performing operation on jobs
 * */

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobsController {

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

	@Value("${aws.IAMRoleARN}")
	private String awsIAMRoleARN;

	// @Autowired
	// private AWSStepFunctions sfnClient;

	@Autowired
	private AWSLambda awsLambda;

	@Autowired
	private ConfigureClientService configureClientService;

	@Autowired
	private SnowflakeService snowflakeService;

	@Autowired
	private DepFlowBuilderJobNameDbService depFlowBuilderJobNameDbService;

	@Autowired
	private DepFlowbuilderJobStatusDbService depFlowbuilderJobStatusDbService;

	@Autowired
	private DepFlowBuilderJobInputDbService depFlowBuilderJobInputDbService;

	@Autowired
	private JobsService jobsService;

	@Autowired
	DepClientDbService depClientDbService;

	// @Autowired
	// AwsStepFunctionConfiguration awsStepFunctionConfiguration;

	/*
	 * API to fetch all jobs batch wise
	 */

	@GetMapping("/get-jobs/{client_id}/{batch_id}")
	public ResponseEntity<?> getJobs(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, HttpServletRequest request,
			HttpServletResponse response) {

		List<DepFlowBuilderJobNameEntity> list = this.depFlowBuilderJobNameDbService.getAllJobs(client_id, batch_id);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	/*
	 * API get approved job
	 */

	@GetMapping("/get-approved-jobs/{client_id}/{batch_id}")

	public ResponseEntity<?> getApprovedJobs(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, HttpServletRequest request,
			HttpServletResponse response) {

		List<DepFlowBuilderJobNameEntity> list = this.depFlowBuilderJobNameDbService.getApprovedJobs(client_id,
				batch_id);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	/*
	 * API to create jobs
	 */

	@PostMapping("/add-job")
	public ResponseEntity<DepFlowBuilderJobNameEntity> createJob(@RequestBody DepFlowBuilderJobNameEntity job,
			@RequestParam String bucket_name,
			HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean exist = this.depFlowBuilderJobNameDbService.addJob(job, bucket_name);

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
	 * API for creating a copy of new job
	 */

	@PostMapping("/copy-job")
	public ResponseEntity<?> copyJob(@RequestBody CopyJobDto job, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			DepClientEntity getClient = depClientDbService.getClientById(job.getClient_id());
			this.depFlowBuilderJobNameDbService.copyNewJob(job, getClient.getDataRegionEntity().getBucket_name());
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	/*
	 * API to update job
	 */
	@PutMapping("/update-job-detail")
	public ResponseEntity<?> updateJob(@RequestBody DepFlowBuilderJobNameEntity job, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = this.depFlowBuilderJobNameDbService.UpdateJob(job);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	/*
	 * API to delete job
	 */

	@DeleteMapping("/delete-job/{client_id}/{batch_id}/{job}")
	public ResponseEntity<?> deleteJob(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, @PathVariable("job") String job,
			HttpServletRequest request, HttpServletResponse response) {

		boolean deleted = this.depFlowBuilderJobNameDbService.deleteCurrentJob(client_id, batch_id, job);

		if (deleted) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to disable particular job
	 */

	@PutMapping("/disable/{client_id}/{batch_id}/{job}")
	public ResponseEntity<?> disableJob(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, @PathVariable("job") String job,
			HttpServletRequest request, HttpServletResponse response) {

		boolean disabled = this.depFlowBuilderJobNameDbService.disableJob(client_id, batch_id, job);

		if (disabled) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to enable particular job
	 */

	@PutMapping("/enable/{client_id}/{batch_id}/{job}")
	public ResponseEntity<?> enableJob(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, @PathVariable("job") String job,
			HttpServletRequest request, HttpServletResponse response) {

		boolean enable = this.depFlowBuilderJobNameDbService.enableJob(client_id, batch_id, job);

		if (enable) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to run particular job
	 */

	@GetMapping("/run-job/{batch}/{batch_id}/{client_id}/{client_name}/{execution_id}/{job_id}/{connectionType}/{connectionName}/{trackingId}")
	public ResponseEntity<?> runJob(@PathVariable("batch") String batch,
			@PathVariable("batch_id") String batch_id,
			@PathVariable("client_id") String client_id,
			@PathVariable("execution_id") String execution_id,
			@PathVariable("job_id") String job_id,
			@PathVariable("client_name") String client_name,
			@PathVariable("connectionType") String connectionType,
			@PathVariable("connectionName") String connectionName,
			@PathVariable("trackingId") String trackingId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		final DepFlowBuilderJobNameEntity getJob = depFlowBuilderJobNameDbService.getJobById(job_id);

		if (connectionType.equalsIgnoreCase(ApplicationConstants.SNOWFLAKE)) {
			final String startTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			try {
				final DepFlowbuilderJobStatusEntity table = new DepFlowbuilderJobStatusEntity();
				table.setExecution_id(execution_id);
				table.setBatch_id(batch_id);
				table.setBatch_name(batch);
				table.setClient_id(client_id);
				table.setClient_name(client_name);
				table.setStart_time(startTime);
				table.setJob_name(getJob.getInput_ref_key());
				table.setStatus("Running");
				table.setEnd_time("");
				table.setExtract(getJob.getExtracts());
				depFlowbuilderJobStatusDbService.saveJobStatus(table);
				configureClientService.createConnection(connectionName, connectionType);
				boolean status = snowflakeService.runJob(batch_id, client_id, getJob.getInput_ref_key());
				if (status) {
					table.setEnd_time(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
					table.setStatus(ApplicationConstants.COMPLETE);
					depFlowbuilderJobStatusDbService.saveJobStatus(table);
				}
			} catch (Exception e) {
				final DepFlowbuilderJobStatusEntity table = new DepFlowbuilderJobStatusEntity();
				table.setStart_time(startTime);
				table.setExecution_id(execution_id);
				table.setBatch_id(batch_id);
				table.setBatch_name(batch);
				table.setClient_id(client_id);
				table.setClient_name(client_name);
				table.setEnd_time(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
				table.setStatus(ApplicationConstants.FAILED);
				table.setStatus("");
				table.setEnd_time("");
				table.setExtract("");
				depFlowbuilderJobStatusDbService.saveJobStatus(table);
				System.out.println("res" + e.getLocalizedMessage());
			}
		} else if (connectionType.equalsIgnoreCase(ApplicationConstants.GLUE)) {
			DepClientEntity getClient = depClientDbService.getClientById(client_id);
			String PREFIX = client_name + "/" + batch + "/" + getJob.getInput_ref_key() + "/Data_Processor/Scripts/"
					+ trackingId + "/" + getJob.getInput_ref_key() + ".json";

			JSONObject input = new JSONObject();
			input.put("rule_engine", false);
			input.put("client_id", client_id);
			input.put("batch_name", batch);
			input.put("job_id", job_id);
			input.put("batch_id", batch_id);
			input.put("execution_id", execution_id);
			input.put("client_name", client_name);
			input.put("job_name", getJob.getInput_ref_key());
			input.put("profile_env_bucket", getClient.getDataRegionEntity().getBucket_name());
			input.put("profile_env", profile_env);
			input.put("log_group", "/aws/vendedlogs/states/CDEP_STATE_MACHINE_1-Logs");
			input.put("prefix", PREFIX);

			String ARN = "";
			if (Objects.equals(getClient.getDataRegionEntity().getData_region_code(), AWSConstants.US_REGION)) {
				ARN = AWSConstants.US_REGION_GLUE_ARN;
			}

			System.out.println(ARN);
			try {

				AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
				AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
				AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;
				AwsStepFunctionConfiguration.awsRegion = getClient.getDataRegionEntity().getData_region_code();

				StartExecutionRequest executionRequest = new StartExecutionRequest()
						.withStateMachineArn(ARN)
						.withInput(input.toString());

				StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
						.startExecution(executionRequest);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {
			// Jobs getJob = this.jobRepo.getJobById(job_id);

			DepClientEntity getClient = depClientDbService.getClientById(client_id);

			JSONObject input = new JSONObject();
			input.put("rule_engine", false);
			input.put("client_id", client_id);
			input.put("batch_name", batch);
			input.put("job_id", job_id);
			input.put("batch_id", batch_id);
			input.put("execution_id", execution_id);
			input.put("client_name", client_name);
			input.put("job_name", getJob.getInput_ref_key());
			input.put("profile_env_bucket", getClient.getDataRegionEntity().getBucket_name());
			input.put("profile_env", profile_env);

			String ARN = getClient.getDataRegionEntity().getData_region_arn();
			try {

				System.out.println(ARN);
				AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
				AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
				AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;
				AwsStepFunctionConfiguration.awsRegion = getClient.getDataRegionEntity().getData_region_code();

				StartExecutionRequest executionRequest = new StartExecutionRequest()
						.withStateMachineArn(ARN)
						.withInput(input.toString());

				StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
						.startExecution(executionRequest);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return ResponseEntity.ok().build();
	}

	/*
	 * API to fetch list of lambda functions for real time stitching
	 */

	@GetMapping("/fetch-lambda")
	public ResponseEntity<?> fetchLambdaFn(HttpServletRequest request, HttpServletResponse response) {
		ListFunctionsResult functionResult = null;

		try {
			functionResult = awsLambda.listFunctions();

			List<FunctionConfiguration> list = functionResult.getFunctions();

			List<Map<String, String>> lambdaName = new ArrayList<>();

			for (Iterator iter = list.iterator(); iter.hasNext();) {
				FunctionConfiguration config = (FunctionConfiguration) iter.next();
				Map<String, String> map = new HashMap<>();
				map.put("lambdaName", config.getFunctionName());
				map.put("lambdaArn", config.getFunctionArn());
				lambdaName.add(map);
			}

			return ResponseEntity.status(HttpStatus.OK).body(lambdaName);
		} catch (ServiceException e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to create step function for selected lambda functions
	 */

	@PostMapping("/create-step-function/{batch}/{job}")
	public ResponseEntity<?> createStepFunction(@RequestBody String stateMachineJson,
			@PathVariable("batch") String batch, @PathVariable("job") String job, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			AwsStepFunctionConfiguration.awsStepFunctionsConfig()
					.createStateMachine(new CreateStateMachineRequest().withName("DEP_" + batch + "_" + job)
							.withRoleArn(awsIAMRoleARN).withDefinition(stateMachineJson));

			return ResponseEntity.status(HttpStatus.OK).build();

		} catch (ServiceException e) {
			System.out.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/get-all-state-machines")
	public ResponseEntity<?> getAllStateMachines() {

		ListStateMachinesResult listStateMachinesResult = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
				.listStateMachines(new ListStateMachinesRequest());

		List<StateMachineListItem> stateMachines = listStateMachinesResult.getStateMachines();

		if (!stateMachines.isEmpty()) {
			stateMachines.forEach(sm -> {
				System.out.println("\t- Name: " + sm.getName());
				System.out.println("\t- Arn: " + sm.getStateMachineArn());
			});
		}

		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	/*
	 * Api to update the extract part of job input
	 */
	@PutMapping("/update-extract-job")
	public ResponseEntity<?> updateExtractJob(@RequestBody JobInputExtractUpdateDto job, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = depFlowBuilderJobInputDbService.UpdateExtractJob(job);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	/*
	 * Api to update job run type connection
	 */

	@PutMapping("/update-job-run-type")
	public ResponseEntity<?> updateConnection(@RequestBody DepFlowBuilderJobNameEntity job, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = depFlowBuilderJobNameDbService.setJobRunType(job);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@GetMapping("/get-all-jobs")
	public ResponseEntity<?> getAllJobs(HttpServletRequest request,
			HttpServletResponse response) {
		final List<DepFlowBuilderJobNameEntity> list = this.depFlowBuilderJobNameDbService.getAllJobs();

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/get-all-job-and-status/{client_id}/{batch_id}")
	public ResponseEntity<?> getAllJobsANdStatus(@PathVariable(value = "client_id") String client_id,
			@PathVariable(value = "batch_id") String batch_id, HttpServletRequest request,
			HttpServletResponse response) {
		final List<FlowBuilderResponseDto> res = jobsService.getAllJobsANdStatus(client_id, batch_id);
		if (res == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/get-job-status/{job_id}/{execution_id}")
	public ResponseEntity<?> getJobsStatus(@PathVariable(value = "job_id") String jobId,
			@PathVariable(value = "execution_id") String execution_id, HttpServletRequest request,
			HttpServletResponse response) {
		final FlowBuilderResponseDto res = jobsService.getJobStatus(jobId, execution_id);
		if (res == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
}
