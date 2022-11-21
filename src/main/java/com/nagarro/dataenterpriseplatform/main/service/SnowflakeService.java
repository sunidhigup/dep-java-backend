package com.nagarro.dataenterpriseplatform.main.service;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;


public interface SnowflakeService {

	/**
	 * 
	 * @param dbName
	 * @return
	 * @throws SQLException
	 */
	public ResponseEntity<?> getDbTables(String dbName) throws SQLException;

	/**
	 * 
	 * @param tableName
	 * @return
	 * @throws InterruptedException
	 * @throws SQLException
	 */

	public ResponseEntity<?> runSqlQuery(String query) throws InterruptedException, SQLException;


	/**
	 * 
	 * @return
	 * @throws Exception
	 */

	public boolean runJob(String batch_id, String client_id,  String job_id) throws Exception;
	
}
