package com.nagarro.dataenterpriseplatform.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.nagarro.dataenterpriseplatform.main.service.GlewService;

@RestController
@RequestMapping("/api/glew")
@CrossOrigin(origins = "*")
public class GlewController {

	@Autowired
	private GlewService glewService;
	
	@GetMapping("/configure-glew-crawler")
	public ResponseEntity<?> createCrawler() {
		return glewService.createCrawler();
	}
	
	@GetMapping("/get-tables")
	public ResponseEntity<?> getDbTables(String dbName) {
		return glewService.getDbTables(dbName);
	}
	
	@GetMapping("/runSqlQuery")
	public ResponseEntity<?> runSqlQuery(@RequestParam String query) throws InterruptedException {
		return glewService.runSqlQuery(query);
	}
	
	@GetMapping("/run-glew-crawler")
	public ResponseEntity<?> runGlewCrawler(@RequestParam String clientName) {
		return glewService.runGlewCrawler(clientName);
	}
	
	@GetMapping("/get-databases")
	public ResponseEntity<?> getDatabases() {
		return glewService.getDatabases();
	}

}
