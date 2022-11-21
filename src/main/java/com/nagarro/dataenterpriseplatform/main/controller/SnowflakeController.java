package com.nagarro.dataenterpriseplatform.main.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.nagarro.dataenterpriseplatform.main.service.SnowflakeService;

@RestController
@RequestMapping("/api/snowflake")
@CrossOrigin(origins = "*")
public class SnowflakeController {

	@Autowired
	private SnowflakeService snowflakeService;
		
	@GetMapping("/get-tables")
	public ResponseEntity<?> getDbTables(String dbName) throws SQLException {
		return snowflakeService.getDbTables(dbName);
	}
	
	@GetMapping("/runSqlQuery")
	public ResponseEntity<?> runSqlQuery(@RequestParam String query) throws InterruptedException, SQLException {
		return snowflakeService.runSqlQuery(query);
	}
	
	
	@GetMapping("/run-job")
	public ResponseEntity<?> runJob(@RequestParam String batch_id,@RequestParam String client_id, @RequestParam String job_id) throws Exception {
		snowflakeService.runJob(batch_id,client_id,job_id);
		return ResponseEntity.ok().build();
	}
}
