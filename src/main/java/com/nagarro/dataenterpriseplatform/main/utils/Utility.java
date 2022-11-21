package com.nagarro.dataenterpriseplatform.main.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.QueryExecutionContext;
import com.amazonaws.services.athena.model.ResultConfiguration;
import com.amazonaws.services.athena.model.StartQueryExecutionRequest;
import com.amazonaws.services.athena.model.StartQueryExecutionResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nagarro.dataenterpriseplatform.main.AWS.multitenant.AWSS3Configuration;

public class Utility {

    public static String submitAthenaQuery(String dbName, String queryOutputLocation, String query,
            AmazonAthena athenaClient) {
        StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest();
        final QueryExecutionContext queryExecutionContext = new QueryExecutionContext();
        queryExecutionContext.withDatabase(dbName);
        final ResultConfiguration resultConfiguration = new ResultConfiguration();
        resultConfiguration.setOutputLocation(queryOutputLocation);
        startQueryExecutionRequest = startQueryExecutionRequest.withQueryString(query)
                .withQueryExecutionContext(queryExecutionContext).withResultConfiguration(resultConfiguration);
        StartQueryExecutionResult result = athenaClient.startQueryExecution(startQueryExecutionRequest);

        return result.getQueryExecutionId();
    }

    public static String getEncodedPassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.encode(password);
    }

    public static List<JSONObject> getAllS3List(String bucketname,
            AmazonS3 amazonS3,
            String client_name,
            String batch_name,
            String table_name,
            String source) {
        String PREFIX = "";
        if (Objects.equals(source, "tablerule")) {
            PREFIX = client_name + "/" + batch_name + "/" + table_name + "/Rule_engine/Input/";
        } else {
            PREFIX = client_name + "/" + batch_name + "/" + table_name + "/Preprocess_Engine/Failed/";
        }

        final ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucketname)
                .withPrefix(PREFIX)
                .withDelimiter("/");

        final ObjectListing response = amazonS3.listObjects(request);
        final List<JSONObject> s3data = new ArrayList<>();
        final List<String> list = response.getCommonPrefixes();
        for (String prefixes : list) {
            JSONObject input = new JSONObject();
            input.put("label", prefixes.split("/")[5]);
            input.put("value", prefixes);
            s3data.add(input);
        }
        return s3data;
    }

    public static List<JSONObject> getEachS3List(String bucketname, AmazonS3 amazonS3, String prefix) {
        final String PREFIX = prefix;
        final ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucketname).withPrefix(PREFIX)
                .withDelimiter("/");
        final ObjectListing response = amazonS3.listObjects(request);
        final List<JSONObject> s3data = new ArrayList<>();
        for (S3ObjectSummary res : response.getObjectSummaries()) {
            if (res.getKey().split(PREFIX).length == 2) {
                JSONObject input = new JSONObject();
                input.put("label", res.getKey().split(PREFIX)[1].split("/")[0]);
                input.put("value", PREFIX + res.getKey().split(PREFIX)[1].split("/")[0] + "/");
                s3data.add(input);
            }
        }
        return s3data;
    }

    public static List<JSONObject> getAllS3ListFlowBuilder(String bucketname, AmazonS3 amazonS3, String client_name,
            String batch_name,
            String table_name,
            String TimestampType) {
        String PREFIX = "";
        if (TimestampType.equals("RuleEngine")) {
            PREFIX = client_name + "/" + batch_name + "/" + table_name + "/Rule_engine/Output/valid/";
        } else {
            PREFIX = client_name + "/" + batch_name + "/" + table_name + "/Data_Processor/Input/";
        }

        System.out.println(PREFIX);
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucketname)
                .withPrefix(PREFIX)
                .withDelimiter("/");

        ObjectListing response = amazonS3.listObjects(request);
        List<JSONObject> s3data = new ArrayList<>();
        List<String> list = response.getCommonPrefixes();

        for (String prefixes : list) {
            JSONObject input = new JSONObject();
            String splitvalue = "";
            if (TimestampType.equals("RuleEngine")) {
                splitvalue = prefixes.split("/")[6];
            } else {
                splitvalue = prefixes.split("/")[5];
            }
            input.put("label", splitvalue);
            input.put("value", prefixes);
            s3data.add(input);
        }
        return s3data;
    }

}
