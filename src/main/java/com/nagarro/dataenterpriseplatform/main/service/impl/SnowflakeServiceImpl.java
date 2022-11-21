package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobInputEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobInputDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderMetadataDbService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.service.SnowflakeService;

import net.snowflake.client.jdbc.SnowflakeConnectionV1;

import com.amazonaws.services.s3.model.S3Object;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AmazonS3Configuration;
import com.nagarro.dataenterpriseplatform.main.SnowFlake.config.SnowFlakeConfiguration;

@Service
public class SnowflakeServiceImpl implements SnowflakeService {
	
	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.sessionToken}")
	private String sessionToken;

	@Value("${aws.bucketName}")
	private String awsBucketName;


	@Autowired
	private AmazonS3Configuration amazonS3Configuration;

	@Autowired
	private DepClientDbService clientRepository;

	@Autowired
	private DepFlowBuilderMetadataDbService depFlowBuilderMetadataDbService;

	@Autowired
	private DepFlowBuilderJobInputDbService depFlowBuilderJobInputDbService;

	private String alise = null;
	
	private Connection connection = null;


	@Override
	public ResponseEntity<?> getDbTables(String dbName) throws SQLException {
		final Connection snowConnection = SnowFlakeConfiguration.connection;
		final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final String sql = "show tables";
		final PreparedStatement statement = snowConnection.prepareStatement(sql);
		final ResultSet res = statement.executeQuery();
		final ResultSetMetaData metaData = statement.getResultSet().getMetaData();
		int columnCount = metaData.getColumnCount();
		while (res.next()) {
			Map<String, Object> columns = new LinkedHashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				columns.put(metaData.getColumnLabel(i), res.getObject(i));
			}
			rows.add(columns);
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, rows);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> runSqlQuery(String query) throws InterruptedException, SQLException {
		final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final Connection snowConnection = this.connection;
		String sql = snowConnection.nativeSQL(query);
		final Statement statement = snowConnection.createStatement();
		final ResultSet res = statement.executeQuery(sql);
		final ResultSetMetaData metaData = statement.getResultSet().getMetaData();
		int columnCount = metaData.getColumnCount();
		while (res.next()) {
			Map<String, Object> columns = new LinkedHashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				columns.put(metaData.getColumnLabel(i), res.getObject(i));
			}
			rows.add(columns);
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, rows);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

	@Override
	public boolean runJob(String batch_id, String client_id, String jobName) throws Exception {

			this.connection  = SnowFlakeConfiguration.connection;
			final DepClientEntity clinetInfo = clientRepository.getClientById(client_id);
			final DepFlowBuilderMetadataEntity batchInfo = depFlowBuilderMetadataDbService.getBatchbyId(batch_id);
			final DepFlowBuilderJobInputEntity jobInfo = depFlowBuilderJobInputDbService.getJobInput(client_id, batch_id, jobName);
			String[] jobName_new = jobInfo.getJob().split("-");
						final String createS3FilePathKey = clinetInfo.getClient_name() + "/" + batchInfo.getBatch_name() + "/"
					+ jobName_new[0] + "/Data_Processor/Scripts/" + jobName_new[0].concat(".json");

			final String createS3MainFileHeaderPathKey = clinetInfo.getClient_name() + "/" + batchInfo.getBatch_name()
					+ "/" + jobName_new[0]+ "/Rule/" + jobName_new[0].concat(".json");

			final String createS3OtherFileHeaderPathKey = clinetInfo.getClient_name() + "/" + batchInfo.getBatch_name()
					+ "/" +jobName_new[0] + "/Rule/";

			final S3Object s3FileObject = gets3Object(jobInfo.getBucket(), createS3FilePathKey);
			System.out.println("res"+s3FileObject);

			byte[] response = IOUtils.toByteArray(s3FileObject.getObjectContent());
			final JSONObject jsonObject = new JSONObject(new String(response));
			processJson(jsonObject, jobInfo.getBucket(), createS3MainFileHeaderPathKey, createS3OtherFileHeaderPathKey);
		 
		return true;

	}

	private S3Object gets3Object(String bucketName, String s3Key) {
		System.out.println("res"+s3Key);
		return amazonS3Configuration.getAmazonS3Client().getObject(bucketName, s3Key);
	}

	public boolean processJson(JSONObject json, String bucketName, String headerPath, String headerOtherPath)
			throws IOException, JSONException, SQLException, InterruptedException {
		System.out.println("res");
		final JSONArray object = (JSONArray) json.get(ApplicationConstants.STEPS);
		createDataBaseAndWareHouse();
		for (int i = 0; i < object.length(); i++) {
			final JSONObject newObj = object.getJSONObject(i);
			switch (newObj.get(ApplicationConstants.METHOD_NAME).toString()) {
			case ApplicationConstants.READ:
				parseJsonReadBlock(newObj, headerPath, headerOtherPath, bucketName);
				break;
			case ApplicationConstants.RULE_ENGLINE:
				parseJsonRuleBlock(newObj);
				break;
			case ApplicationConstants.EXECUTE_SQL:
				parseJsonExecuteSqlBlock(newObj);
				break;
			case ApplicationConstants.MULTI_TABLE_JOIN:
				parseJsonJoinBlock(newObj);
				break;
			case ApplicationConstants.WRITE:
				parseJsonWriteBlock(newObj);
				break;

			}

		}
		return true;
	}

	public JSONObject readS3JsonObjectHeaders(String bucketName, String path) throws IOException {
		final S3Object s3FileObject = gets3Object(bucketName, path);
		final InputStream inputStream = s3FileObject.getObjectContent();
		return new JSONObject(IOUtils.toString(inputStream));
	}

	private boolean createTableAndDumbData(String headers, String tableName, String path, String fields)
			throws SQLException {

		final SnowflakeConnectionV1 snowConnection = (SnowflakeConnectionV1) this.connection;
		final Statement stmt = snowConnection.createStatement();
		final String sqll = "create or replace table " + tableName + " (" + headers + ")";
		stmt.execute(sqll);
		final S3Object s3FileObject = gets3Object(awsBucketName,
				StringUtils.substringAfter(StringUtils.substringAfter(path, "//"), "/"));
		final InputStream inputStream = s3FileObject.getObjectContent();
		final String createStage = buildCreateStageStatement();
		stmt.execute(createStage);
		snowConnection.uploadStream("COPYIN_STAGE", "", inputStream, path.substring(path.lastIndexOf("/")), false);
		stmt.execute("USE WAREHOUSE " + "DEP");
		String sql = String.format(
				"copy into %s(%s) from @COPYIN_STAGE/%s file_format = ( type='CSV', skip_header=1) ON_ERROR=CONTINUE purge="
						+ true + "   ",
				tableName, fields, path.substring(path.lastIndexOf("/")));
		stmt.execute(sql);
		snowConnection.commit();
		return false;

	}

	private boolean createDataBaseAndWareHouse() throws SQLException {
		final SnowflakeConnectionV1 snowConnection = (SnowflakeConnectionV1) this.connection;
		final Statement stmt = snowConnection.createStatement();
		String dsdsdsd = "CREATE  or replace WAREHOUSE DEP   WITH WAREHOUSE_SIZE = 'XSMALL' WAREHOUSE_TYPE = 'STANDARD' AUTO_SUSPEND = 600 AUTO_RESUME = TRUE";
		stmt.execute(dsdsdsd);
		String sssss = "CREATE or replace DATABASE DEP_DB   COMMENT = 'The Lab Database' DATA_RETENTION_TIME_IN_DAYS = 1";
		stmt.execute(sssss);
		return true;
	}

	private String buildCreateStageStatement() {
		return "CREATE OR REPLACE TEMPORARY STAGE COPYIN_STAGE " + "file_format = ( type ='CSV')";
	}

	private void createView(String query, String viewName) throws SQLException {
		final SnowflakeConnectionV1 snowConnection = (SnowflakeConnectionV1) this.connection;
		final Statement stmt = snowConnection.createStatement();
		final String sqll = "create view " + viewName + " as " + query;
		stmt.execute(sqll);
	}

	private void alterView(String oldViewName, String newViewName) throws SQLException {
		final SnowflakeConnectionV1 snowConnection = (SnowflakeConnectionV1) this.connection;
		final Statement stmt = snowConnection.createStatement();
		final String sqll = "alter view " + oldViewName + " rename to " + newViewName;
		stmt.execute(sqll);
	}

	public void writeToS3(String df, String path) throws SQLException {
		final SnowflakeConnectionV1 snowConnection = (SnowflakeConnectionV1) this.connection;
		final Statement stmt = snowConnection.createStatement();
		final String createStage = buildCreateStageStatement();
		stmt.execute(createStage);
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		String sql = String.format("copy into '" + path+"/"+timeStamp + ".csv' from " + df
				+ " HEADER = TRUE credentials = (aws_key_id='"+accessKey+"' aws_secret_key='"+secretKey+"' aws_token='"+sessionToken+"') file_format = ( type='CSV' COMPRESSION=NONE FIELD_DELIMITER = '\t')");
		stmt.execute(sql);
	}

	public void parseJsonReadBlock(JSONObject newObj, String headerPath, String headerOtherPath, String bucketName)
			throws JSONException, IOException, SQLException {
		final JSONObject params = (JSONObject) newObj.get(ApplicationConstants.PARAMS);
		final JSONArray defArray = (JSONArray) params.get(ApplicationConstants.DEFINITIONS);
		for (int j = 0; j < defArray.length(); j++) {
			final JSONObject innerObj = (JSONObject) defArray.get(j);
			JSONObject csvHeadersObject = new JSONObject();
			if (innerObj.get(ApplicationConstants.STEP_NUMBER).toString().equalsIgnoreCase("1")) {
				csvHeadersObject = readS3JsonObjectHeaders(bucketName, headerPath);
			} else {
				csvHeadersObject = readS3JsonObjectHeaders(bucketName,
						headerOtherPath + innerObj.get(ApplicationConstants.DF).toString().concat(".json"));
			}
			final JSONArray csvHeaderArray = (JSONArray) csvHeadersObject.get(ApplicationConstants.FIELDS);
			final LinkedHashMap<String, Object> headerMap = new LinkedHashMap<>();
			for (int l = 0; l < csvHeaderArray.length(); l++) {
				final JSONObject fieldNameObject = (JSONObject) csvHeaderArray.get(l);
				headerMap.put(fieldNameObject.get(ApplicationConstants.FIELD_NAME).toString(),
						fieldNameObject.get(ApplicationConstants.TYPE));
			}
			createTableColumn(innerObj,headerMap);	
		}
	}

	public void parseJsonRuleBlock(JSONObject newObj) throws JSONException, SQLException {
		final JSONObject params = (JSONObject) newObj.get(ApplicationConstants.PARAMS);
		alterView(params.get(ApplicationConstants.TABLE_NAME).toString().concat("_view"),
				params.get(ApplicationConstants.ALIAS).toString().concat("_view"));
		this.alise = params.get(ApplicationConstants.ALIAS).toString();
	}

	public void parseJsonJoinBlock(JSONObject newObj) throws JSONException, SQLException {
		final JSONObject params = (JSONObject) newObj.get(ApplicationConstants.PARAMS);
		final JSONObject tables = (JSONObject) params.get(ApplicationConstants.TABLES);
		final JSONObject joins = (JSONObject) params.get(ApplicationConstants.JOINS);
		final String joinConditions = params.get(ApplicationConstants.JOIN_CONDITION).toString();
		String join = "";
		String joinSql = "";
		for (int k = 1; k <= tables.length(); k++) {
			final String table = tables.get(ApplicationConstants.TABLE + k).toString() + " as "
					+ tables.get(ApplicationConstants.TABLE + k).toString() + " ";
			if (joins.has(ApplicationConstants.JOIN + k)) {
				join += table + joins.get(ApplicationConstants.JOIN + k) + " join ";
			}
			joinSql = join + table + " ON " + joinConditions;
		}
		final String joinQuery = "SELECT " + params.get(ApplicationConstants.SELECT_COLS).toString() + " from "
				+ joinSql;
		final String finalQuery = joinQuery.replaceAll(this.alise, this.alise.concat("_view"));
		createView(finalQuery, params.get(ApplicationConstants.DF).toString().concat("_view"));
	}

	public void parseJsonExecuteSqlBlock(JSONObject newObj) throws JSONException, SQLException {
		final JSONObject params = (JSONObject) newObj.get(ApplicationConstants.PARAMS);
		final JSONArray defArray = (JSONArray) params.get(ApplicationConstants.DEFINITIONS);
		for (int j = 0; j < defArray.length(); j++) {
			JSONObject innerObj = (JSONObject) defArray.get(j);
			final String subString = StringUtils
					.substringAfter(innerObj.get(ApplicationConstants.STATEMENT).toString().toLowerCase(), " from ");
			final String tableName = subString.split(" ")[0];
			String finalQuery = innerObj.get(ApplicationConstants.STATEMENT).toString().replaceFirst(tableName,
					tableName.concat("_view"));
			createView(finalQuery, innerObj.get(ApplicationConstants.DF).toString().concat("_view"));
		}
	}

	public void parseJsonWriteBlock(JSONObject newObj) throws JSONException, SQLException {
		final JSONObject params = (JSONObject) newObj.get(ApplicationConstants.PARAMS);
		writeToS3(params.get(ApplicationConstants.DF).toString().concat("_view"),
				params.get(ApplicationConstants.PATH).toString());
	}
	
	public void createTableColumn(JSONObject innerObj,LinkedHashMap<String, Object> headerMap) throws JSONException, SQLException {
		String csvHeaderString = "";
		String fields = "";
		final String headerString = innerObj.get(ApplicationConstants.HEADER).toString().replaceAll("[^\\x00-\\x7F]", "");
		final List<String> elephantList = Arrays.asList(headerString.split(","));
		String type = "";
		for (String header : elephantList) {
			if (headerMap.get(header) == null) {
				type = "String";
			} else {
				type = headerMap.get(header).toString();
			}
			csvHeaderString = csvHeaderString + "," + header + " " + type;
			fields = fields + "," + header;
		}
		final String finalCsvHeaderString = csvHeaderString.replaceFirst("^,", "");
		final String finalFields = fields.replaceFirst("^,", "");
		createTableAndDumbData(finalCsvHeaderString, innerObj.get(ApplicationConstants.DF).toString(), innerObj.get(ApplicationConstants.PATH).toString(),
				finalFields);
		String select = "SELECT "
				+ innerObj.get(ApplicationConstants.SELECT_COLS).toString().replaceAll("[^\\x00-\\x7F]", "")
				+ " FROM " + innerObj.get(ApplicationConstants.DF);
		createView(select, innerObj.get(ApplicationConstants.DF).toString().concat("_view"));
	}

}
