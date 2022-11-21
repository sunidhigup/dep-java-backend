package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.CreateSecretRequest;
import com.amazonaws.services.secretsmanager.model.CreateSecretResult;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ListSecretsRequest;
import com.amazonaws.services.secretsmanager.model.ListSecretsResult;
import com.amazonaws.services.secretsmanager.model.SecretListEntry;
import com.amazonaws.services.secretsmanager.model.UpdateSecretRequest;
import com.amazonaws.services.secretsmanager.model.UpdateSecretResult;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepConnectionEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepClientRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepConnectionRepo;
import com.nagarro.dataenterpriseplatform.main.dto.AWSConnectionDto;
import com.nagarro.dataenterpriseplatform.main.dto.SnowFlakeConnectionDto;

@Service
@Transactional
public class DepConnectionDbService {

	@Autowired
	private DepConnectionRepo repo;

	@Autowired
	private AWSSecretsManager aWSSecretsManager;

	public boolean CheckSecret(String secretkey) {
		ListSecretsRequest request = new ListSecretsRequest();
		ListSecretsResult response = aWSSecretsManager.listSecrets(request);

		for (SecretListEntry res : response.getSecretList()) {
			if (res.getName().equals(secretkey)) {
				return true;
			}
		}
		return false;
	}

	public boolean CreateAwsCredentialSecretmanager(String secretkey, AWSConnectionDto conn) {
		try {

			final JSONObject input = new JSONObject();
			input.put("awsAccessKey", conn.getAwsAccessKey());
			input.put("awsSecretKey", conn.getAwsSecretKey());
			final String res = input.toString();

			final CreateSecretRequest request = new CreateSecretRequest();
			request.setName(secretkey);
			request.setSecretString(res);
			aWSSecretsManager.createSecret(request);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean CreateSnowflakeCredentialSecretmanager(String secretkey, SnowFlakeConnectionDto conn) {
		try {

			JSONObject input = new JSONObject();
			input.put("user", conn.getUser());
			input.put("password", conn.getPassword());
			input.put("db", conn.getDb());
			input.put("role", conn.getRole());
			input.put("warehouse", conn.getWarehouse());

			String res = input.toString();

			CreateSecretRequest request = new CreateSecretRequest();
			request.setName(secretkey);
			request.setSecretString(res);
			aWSSecretsManager.createSecret(request);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean UpdateAwsCredentialSecretmanager(String secretkey, AWSConnectionDto conn) {
		try {

			JSONObject input = new JSONObject();
			input.put("awsAccessKey", conn.getAwsAccessKey());
			input.put("awsSecretKey", conn.getAwsSecretKey());

			String res = input.toString();

			UpdateSecretRequest request = new UpdateSecretRequest();
			request.setSecretId(secretkey);
			request.setSecretString(res);

			aWSSecretsManager.updateSecret(request);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean UpdateSnowflakeCredentialSecretmanager(String secretkey, SnowFlakeConnectionDto conn) {
		try {

			JSONObject input = new JSONObject();
			input.put("user", conn.getUser());
			input.put("password", conn.getPassword());
			input.put("db", conn.getDb());
			input.put("role", conn.getRole());
			input.put("warehouse", conn.getWarehouse());
			String res = input.toString();

			UpdateSecretRequest request = new UpdateSecretRequest();
			request.setSecretId(secretkey);
			request.setSecretString(res);

			aWSSecretsManager.updateSecret(request);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ResponseEntity<?> GetSecret(String secretkey) {
		final HashMap<String, Object> apiResponse = new HashMap<>();

		GetSecretValueRequest request = new GetSecretValueRequest();
		request.withSecretId(secretkey);
		final GetSecretValueResult response = aWSSecretsManager.getSecretValue(request);
		apiResponse.put("response", response.getSecretString());
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	public boolean CreateAwsSnowflakeCredentialDynamo(DepConnectionEntity conn) {

		final List<DepConnectionEntity> latestReplies = repo.findByConnectionName(conn.getConnectionName());
		if (latestReplies.size() > 0) {
			return true;
		}
		repo.save(conn);
		return false;
	}

	public List<DepConnectionEntity> GetConnectionDetails(String connectionType, String userName) {

		List<DepConnectionEntity> allConnection = repo.findByConnectionNameAndUserName(userName, connectionType);

		return allConnection;
	}

}
