package com.nagarro.dataenterpriseplatform.main.service;

import org.springframework.http.ResponseEntity;

public interface GlewService {

	/**
	 * 
	 * @param dbName
	 * @return
	 */
	public ResponseEntity<?> getDbTables(String dbName);
	
	/**
	 * 
	 * @param tableName
	 * @return
	 * @throws InterruptedException 
	 */
	
	public ResponseEntity<?> runSqlQuery(String query) throws InterruptedException;
	
	/**
	 * 
	 * @return
	 */
	
	public ResponseEntity<?> createCrawler();
	
	/**
	 * 
	 * @param clientName
	 * @return
	 */
	public ResponseEntity<?> runGlewCrawler(String clientName);
	
	
	/**
	 * 
	 * @param 
	 * @return
	 */
	public ResponseEntity<?> getDatabases();
}
