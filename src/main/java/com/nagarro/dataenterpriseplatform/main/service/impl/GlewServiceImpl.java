package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.GetQueryExecutionRequest;
import com.amazonaws.services.athena.model.GetQueryExecutionResult;
import com.amazonaws.services.athena.model.GetQueryResultsRequest;
import com.amazonaws.services.athena.model.GetQueryResultsResult;
import com.amazonaws.services.athena.model.QueryExecutionState;
import com.amazonaws.services.glue.AWSGlue;
import com.amazonaws.services.glue.model.Database;
import com.amazonaws.services.glue.model.GetDatabasesRequest;
import com.amazonaws.services.glue.model.GetTablesRequest;
import com.amazonaws.services.glue.model.GetTablesResult;
import com.amazonaws.services.glue.model.StartCrawlerResult;
import com.amazonaws.services.glue.model.Table;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsGlueConfiguration;
import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepTableRulesDbService;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.service.GlewService;
import com.nagarro.dataenterpriseplatform.main.utils.Utility;

@Service
public class GlewServiceImpl implements GlewService {

	@Autowired
	private AWSGlue awsGlueClient;

	@Autowired
	private AwsGlueConfiguration awsGlueConfiguration;

	@Autowired
	private DepClientDbService clientRepository;

	@Autowired
	private DepFlowBuilderMetadataDbService depFlowBuilderMetadata;

	@Autowired
	private DepTableRulesDbService tableRuleRepository;

	@Autowired
	private AmazonAthena amazonAthena;

	@Value("${aws.glue.glewDbName}")
	private String awsGlewDbName;

	@Value("${aws.glue.IAMRoleARN}")
	private String awgGlewIamRole;

	@Value("${aws.glue.s3GlewTargetPath}")
	private String s3GlewTargetPath;

	@Value("${aws.glue.glewCrawlerName}")
	private String glewCrawlerName;

	@Value("${aws.athena.queryOuputLocation}")
	private String athenaQueryOuputLocation;

	@Override
	public ResponseEntity<?> getDbTables(String dbName) {

		List<HashMap<String, Object>> tableSchemaList = new ArrayList<>();
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final GetTablesRequest tableRequest = new GetTablesRequest().withDatabaseName(dbName);
		final GetTablesResult response = awsGlueClient.getTables(tableRequest);
		final List<Table> tableList = response.getTableList();
		for (Table table : tableList) {
			final HashMap<String, Object> resp = new HashMap<>();
			resp.put(ApplicationConstants.TABLE_NAME, table.getName());
			resp.put(ApplicationConstants.COLUMNS, table.getStorageDescriptor().getColumns());
			tableSchemaList.add(resp);
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, tableSchemaList);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> createCrawler() {
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final HashMap<String, Object> resp = new HashMap<>();


		final List<DepClientEntity> allCLientResponse = clientRepository.getAllClient();
		for (DepClientEntity client : allCLientResponse) {
			final List<DepFlowBuilderMetadataEntity> allBatchResponse = depFlowBuilderMetadata.getAllBatch(client.getClient_id());
			if (allBatchResponse != null && !allBatchResponse.isEmpty()) {
				for (DepFlowBuilderMetadataEntity batch : allBatchResponse) {
					final List<DepTableRulesEntity> tableRuleResponse = tableRuleRepository
							.fetchTableRule(client.getClient_id(), batch.getBatch_name());
					if (tableRuleResponse != null && !tableRuleResponse.isEmpty()) {
						for (DepTableRulesEntity table : tableRuleResponse) {
							final StartCrawlerResult response = awsGlueConfiguration.createGlueCrawler(awsGlueClient,
									awgGlewIamRole,
									s3GlewTargetPath
											.concat(table.getBatchname().concat("/".concat(table.getTablename()))),
									awsGlewDbName, glewCrawlerName);
							resp.put(
									client.getClient_name().concat(
											"_".concat(batch.getBatch_name().concat("_").concat(table.getBatchname()))),
									response);
						}
					}
				}
			}
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, resp);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> runSqlQuery(String query) throws InterruptedException {
		final String queryExecutionId = Utility.submitAthenaQuery(awsGlewDbName, athenaQueryOuputLocation, query,
				amazonAthena);
		boolean isQueryStillRunning = true;
		GetQueryExecutionResult getQueryExecutionResponse = new GetQueryExecutionResult();
		final GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
				.withQueryExecutionId(queryExecutionId);
		while (isQueryStillRunning) {
			getQueryExecutionResponse = amazonAthena.getQueryExecution(getQueryExecutionRequest);
			String queryState = getQueryExecutionResponse.getQueryExecution().getStatus().getState().toString();
			if (queryState.equals(QueryExecutionState.FAILED.toString())) {
				throw new RuntimeException("The Amazon Athena query failed to run with error message: "
						+ getQueryExecutionResponse.getQueryExecution().getStatus().getStateChangeReason());
			} else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
				throw new RuntimeException("The Amazon Athena query was cancelled.");
			} else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
				isQueryStillRunning = false;
			} else {
				// Sleep an amount of time before retrying again.
				Thread.sleep(ApplicationConstants.SLEEP_AMOUNT_IN_MS);
			}
		}
		final GetQueryResultsRequest getQueryResultsRequest = new GetQueryResultsRequest()
				.withQueryExecutionId(queryExecutionId);
		GetQueryResultsResult result = amazonAthena.getQueryResults(getQueryResultsRequest);

		HashMap<String, Object> apiResponse = new HashMap<>();
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.QUERY_EXECUTION_ID, queryExecutionId);

		apiResponse.put(ApplicationConstants.DATA, result.getResultSet().getRows());
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> runGlewCrawler(String clientName) {
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final HashMap<String, Object> resp = new HashMap<>();
		awsGlueConfiguration.createGlueDatabase(clientName);
		final StartCrawlerResult response = awsGlueConfiguration.createGlueCrawler(awsGlueClient, awgGlewIamRole,
				s3GlewTargetPath.concat(clientName), clientName, glewCrawlerName);
		resp.put(glewCrawlerName, response);
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, resp);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getDatabases() {
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final GetDatabasesRequest getDatabasesRequest = new GetDatabasesRequest();
		final List<Database> response = awsGlueClient.getDatabases(getDatabasesRequest).getDatabaseList();
		final List<String> resp = new ArrayList<>();
		for (Database database : response) {
			resp.add(database.getName());
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, resp);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

}
