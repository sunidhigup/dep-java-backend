package com.nagarro.dataenterpriseplatform.main.service;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface ConfigureClientService {
	
	/**
	 * 
	 * @param connectionName
	 * @param connectionType
	 * @return
	 * @throws SQLException
	 */

	public ResponseEntity<?> createConnection(String connectionName, @RequestParam String connectionType ) throws SQLException;
}
