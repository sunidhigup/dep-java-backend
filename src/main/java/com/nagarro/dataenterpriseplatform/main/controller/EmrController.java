package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.Application;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.nagarro.dataenterpriseplatform.main.constants.EmrConstants;
import com.nagarro.dataenterpriseplatform.main.dto.EmrClusterDetails;

@RestController
@RequestMapping("/api/emr")
@CrossOrigin(origins = "*")
public class EmrController {

	@Autowired
	AmazonElasticMapReduce emr;

	@PostMapping("/create-emr-cluster")
	public ResponseEntity<?> createEmrCluster(@RequestBody Map<String, String> cluster) {

		System.out.println(cluster);
		// create a step to enable debugging in the AWS Management Console
		StepFactory stepFactory = new StepFactory();

		HadoopJarStepConfig sparkStepConf = new HadoopJarStepConfig().withJar("command-runner.jar")
				.withArgs("spark-submit").withArgs(
						"--jars s3://cdep/emr/spark-sql-kinesis_2.12-1.2.0_spark-3.0.jar --py-files s3://cdep/Streaming/Streaming.zip,s3://cdep/Streaming/Rule_Engine.zip,s3://cdep/Streaming/data_processor.zip s3://cdep/Streaming/kinesis_script.py");

		StepConfig enabledebugging = new StepConfig().withName(EmrConstants.STEP_NAME)
				.withActionOnFailure(EmrConstants.ACTION_ON_FAILURE).withHadoopJarStep(sparkStepConf);

		Application spark = new Application().withName(EmrConstants.APPLICATION);

		RunJobFlowRequest request = new RunJobFlowRequest().withName(cluster.get("clusterName"))

				.withReleaseLabel(EmrConstants.RELEASE_LABEL).withSteps(enabledebugging).withApplications(spark)

				.withLogUri(EmrConstants.LOG_URI)

				.withServiceRole(EmrConstants.SERVICE_ROLE)

				.withJobFlowRole(EmrConstants.JOB_FLOW_ROLE)

				.withInstances(new JobFlowInstancesConfig().withEc2SubnetId(EmrConstants.SUBNET_ID)
						.withEc2KeyName(EmrConstants.EC2KEY_NAME).withInstanceCount(3)
						.withKeepJobFlowAliveWhenNoSteps(true).withMasterInstanceType(EmrConstants.INSTANCE_TYPE)
						.withSlaveInstanceType(EmrConstants.INSTANCE_TYPE));

		RunJobFlowResult result = emr.runJobFlow(request);

		return ResponseEntity.status(HttpStatus.OK).body(result.getJobFlowId());
	}

	@GetMapping("/terminate-emr-cluster/{clusterId}")
	public ResponseEntity<?> terminateEmrCluster(@PathVariable String clusterId) {
		TerminateJobFlowsRequest request = new TerminateJobFlowsRequest().withJobFlowIds(clusterId);
		TerminateJobFlowsResult result = emr.terminateJobFlows(request);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/get-cluster-status")
	public ResponseEntity<?> listCluster(@RequestParam(required = false) String clusterId,
			@RequestParam(required = false) String clusterName) {

		EmrClusterDetails cluster = new EmrClusterDetails();

		if (clusterName == null && clusterId == null) {

			cluster.setClusterId("unknown");
			cluster.setClusterName("unknown");
			cluster.setStatus("unknown");
			return ResponseEntity.status(HttpStatus.OK).body(cluster);

		}

		List<ClusterSummary> result = emr.listClusters().getClusters();

		if (clusterName != null && clusterName.length() > 1) {

			result = result.stream().filter((element) -> element.getName().equals(clusterName))
					.collect(Collectors.toList());

		}

		List<ClusterSummary> runningCluster = result.stream()
				.filter((e) -> e.getStatus().getState().equals("STARTING") || e.getStatus().getState().equals("RUNNING")
						|| e.getStatus().getState().equals("WAITING") || e.getStatus().getState().equals("TERMINATING"))
				.collect(Collectors.toList());

		if (!runningCluster.isEmpty()) {
			result = runningCluster;
		}
		if (clusterId != null && !clusterId.equals("unknown")) {

			result = result.stream().filter((element) -> (element.getId().equals(clusterId))

			).collect(Collectors.toList());
		}

		result.stream().forEach((e) -> {

			cluster.setClusterId(e.getId());
			cluster.setClusterName(e.getName());
			cluster.setStatus(e.getStatus().getState());

		});

		if (result.isEmpty()) {

			cluster.setClusterId("unknown");
			cluster.setClusterName("unknown");
			cluster.setStatus("unknown");
		}
		return ResponseEntity.status(HttpStatus.OK).body(cluster);
	}

}
