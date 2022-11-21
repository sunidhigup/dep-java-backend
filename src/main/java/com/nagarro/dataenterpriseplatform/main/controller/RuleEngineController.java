package com.nagarro.dataenterpriseplatform.main.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsStepFunctionConfiguration;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Controller class for performing rule engine operations
 * */

@RestController
@RequestMapping("/api/rule-engine")
@CrossOrigin(origins = "*")
public class RuleEngineController {
    @Value("${aws.accessKey}")
    private String dynamodbAccessKey;

    @Value("${aws.secretKey}")
    private String dynamodbSecretKey;

    @Value("${aws.sessionToken}")
    private String dynamodbSessionToken;

    @Value("${profile_env}")
    private String profile_env;

    @Value("${aws.stateMachineARN}")
    private String awsStateMachineARN;

    @Value("${aws.region.euStateMachineARN}")
    private String euAWSStateMachineARN;

//    @Value("${aws.bucketName}")
//    private String awsBucketName;

    @Autowired
    private AmazonS3 amazonS3;

    // @Autowired
    // private AWSStepFunctions sfnClient;

    @Autowired
    DepClientDbService depClientDbService;

    /*
     * API for running whole rule engine
     */

    @GetMapping("/execute-rule-engine/{batch}/{batch_id}/{client_id}/{client_name}/{execution_id}")
    public ResponseEntity<?> executeRuleEngine(@PathVariable("batch") String batch,
            @PathVariable("batch_id") String batch_id, @PathVariable("client_id") String client_id,
            @PathVariable("execution_id") String execution_id, @PathVariable("client_name") String client_name,
            HttpServletRequest request, HttpServletResponse response) {

        DepClientEntity getClient = depClientDbService.getClientById(client_id);

        JSONObject input = new JSONObject();
        input.put("rule_engine", true);
        input.put("batch_name", batch);
        input.put("client_id", client_id);
        input.put("batch_id", batch_id);
        input.put("execution_id", execution_id);
        input.put("client_name", client_name);
         input.put("profile_env_bucket", getClient.getDataRegionEntity().getBucket_name());
//        input.put("profile_env_bucket", awsBucketName);
        input.put("profile_env", profile_env);

        String ARN = getClient.getDataRegionEntity().getData_region_arn();

        AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
        AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
        AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;
        AwsStepFunctionConfiguration.awsRegion = getClient.getDataRegionEntity().getData_region_code();
        StartExecutionRequest executionRequest = new StartExecutionRequest()
                .withStateMachineArn(ARN)
                .withInput(input.toString());

        StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
                .startExecution(executionRequest);

        return ResponseEntity.ok().build();
    }

    /*
     * API for running single rules
     */

    @GetMapping("/execute-table-rule-engine/{batch}/{batch_id}/{client_id}/{client_name}/{execution_id}/{table_name}")
    public ResponseEntity<?> executeTableRuleEngine(@PathVariable("batch") String batch,
            @PathVariable("batch_id") String batch_id, @PathVariable("client_id") String client_id,
            @PathVariable("execution_id") String execution_id, @PathVariable("client_name") String client_name,
            @PathVariable("table_name") String table_name, HttpServletRequest request, HttpServletResponse response) {

        DepClientEntity getClient = depClientDbService.getClientById(client_id);

        JSONObject input = new JSONObject();
        input.put("rule_engine", true);
        input.put("batch_name", batch);
        input.put("table_name", table_name);
        input.put("batch_id", batch_id);
        input.put("client_id", client_id);
        input.put("execution_id", execution_id);
        input.put("client_name", client_name);
         input.put("profile_env_bucket", getClient.getDataRegionEntity().getBucket_name());
//        input.put("profile_env_bucket", awsBucketName);
        input.put("profile_env", profile_env);

        String ARN = getClient.getDataRegionEntity().getData_region_arn();

        AwsStepFunctionConfiguration.awsAccessKey = dynamodbAccessKey;
        AwsStepFunctionConfiguration.awsSecretKey = dynamodbSecretKey;
        AwsStepFunctionConfiguration.sessionToken = dynamodbSessionToken;
        AwsStepFunctionConfiguration.awsRegion = getClient.getDataRegionEntity().getData_region_code();
        StartExecutionRequest executionRequest = new StartExecutionRequest()
                .withStateMachineArn(ARN)
                .withInput(input.toString());

        StartExecutionResult startExecution = AwsStepFunctionConfiguration.awsStepFunctionsConfig()
                .startExecution(executionRequest);

        return ResponseEntity.ok().build();
    }

}
