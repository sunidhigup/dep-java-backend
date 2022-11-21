package com.nagarro.dataenterpriseplatform.main.constants;

public class EmrConstants {

	public static final String STEP_NAME = "dep-spark";
	public static final String ACTION_ON_FAILURE = "CANCEL_AND_WAIT";
	public static final String APPLICATION = "Spark";
	public static final String RELEASE_LABEL = "emr-6.2.0";
	public static final String LOG_URI = "s3://aws-logs-955658629586-us-east-1/elasticmapreduce/";
	public static final String SERVICE_ROLE = "EMR_DefaultRole";
	public static final String JOB_FLOW_ROLE = "EMR_EC2_DefaultRole";
	public static final String SUBNET_ID = "subnet-1ce7fc12";
	public static final String EC2KEY_NAME = "ec2keypair";
	public static final String INSTANCE_TYPE = "m4.large";

}
