package com.nagarro.dataenterpriseplatform.main.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepConnectionEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepConnectionDbService;

import com.nagarro.dataenterpriseplatform.main.dto.AWSConnectionDto;
import com.nagarro.dataenterpriseplatform.main.dto.SnowFlakeConnectionDto;
import com.nagarro.dataenterpriseplatform.main.service.ConfigureClientService;

@RestController
@RequestMapping("/api/connection")
@CrossOrigin(origins = "*")
public class ConfigureClientController {

	@Autowired
	private DepConnectionDbService depConnectionService;

	@Autowired
	private ConfigureClientService service;

	/*
	 * API to check if secretKey exist is secret Manager or not
	 */

	@GetMapping("/check-key-name")
	public ResponseEntity<?> checkSecret(@RequestParam String secretkey) {

		boolean exist = this.depConnectionService.CheckSecret(secretkey);
		if (exist) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to get secret Value from secretKey through secret manager
	 */

	@GetMapping("/get-secret")
	public ResponseEntity<?> getSecret(@RequestParam String secretkey) {
		return this.depConnectionService.GetSecret(secretkey);
	}

	/*
	 * API to get Connection Detail from secretKey through Dynamo
	 */

	@GetMapping("/get-connection")
	public ResponseEntity<?> getConnectionDetails(@RequestParam String connectionType, @RequestParam String userName) {

		final List<DepConnectionEntity> conn = depConnectionService.GetConnectionDetails(connectionType, userName);
		if (conn.size() > 0) {
			return ResponseEntity.of(Optional.of(conn));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to add AWS Connection credential to secret manager
	 */

	@PostMapping("/create-aws-credential-secret-manager")
	public ResponseEntity<?> createAwsCredentialSecretmanager(@RequestParam String secretkey,
			@RequestBody AWSConnectionDto conn) {

		boolean isCreated = this.depConnectionService.CreateAwsCredentialSecretmanager(secretkey, conn);
		if (isCreated) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/*
	 * API to add SnowFlake Connection credential to secret manager
	 */

	@PostMapping("/create-snowflake-credential-secret-manager")
	public ResponseEntity<?> createSnowflakeCredentialSecretmanager(@RequestParam String secretkey,
			@RequestBody SnowFlakeConnectionDto conn) {

		boolean isCreated = this.depConnectionService.CreateSnowflakeCredentialSecretmanager(secretkey, conn);
		if (isCreated) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/*
	 * API to add SnowFlake and AWS Connectionin dynamoDb
	 */

	@PostMapping("/create-aws-snowflake-credential-dynamo")
	public ResponseEntity<?> createAwsSnowflakeCredentialDynamo(@RequestBody DepConnectionEntity conn) {

		boolean exist = this.depConnectionService.CreateAwsSnowflakeCredentialDynamo(conn);
		if (exist) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/update-aws-credential-secret-manager")
	public ResponseEntity<?> updateAwsCredentialSecretmanager(@RequestParam String secretkey,
			@RequestBody AWSConnectionDto conn) {

		boolean isUpdate = this.depConnectionService.UpdateAwsCredentialSecretmanager(secretkey, conn);
		if (isUpdate) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@PutMapping("/update-snowflake-credential-secret-manager")
	public ResponseEntity<?> updateSnowflakeCredentialSecretmanager(@RequestParam String secretkey,
			@RequestBody SnowFlakeConnectionDto conn) {

		boolean isUpdate = this.depConnectionService.UpdateSnowflakeCredentialSecretmanager(secretkey, conn);
		if (isUpdate) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}

	@GetMapping("/create-connection")
	public ResponseEntity<?> createConnection(@RequestParam String connectionName, @RequestParam String connectionType)
			throws SQLException {
		return service.createConnection(connectionName, connectionType);
	}

}
