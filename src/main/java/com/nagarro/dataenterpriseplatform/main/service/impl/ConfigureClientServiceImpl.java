package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.SnowFlake.config.SnowFlakeConfiguration;
import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.service.DepConnectionDbService;
import com.nagarro.dataenterpriseplatform.main.service.ConfigureClientService;

@Service
public class ConfigureClientServiceImpl implements ConfigureClientService {

	@Autowired
	private DepConnectionDbService connRepository;

	@SuppressWarnings("unchecked")
		@Override
		public ResponseEntity<?> createConnection(String connectionName, String connectionType) throws SQLException {
		final HashMap<String, Object> apiResponse = new HashMap<>();
			if(connectionType.equalsIgnoreCase(ApplicationConstants.SNOWFLAKE)) {
				final SnowFlakeConfiguration snowflake =  new SnowFlakeConfiguration();
				final Map<String,Object> response = (Map<String, Object>) connRepository.GetSecret(connectionName).getBody();
				if(response !=null && response.containsKey("response")) {
					

					final String secretString = response.get("response").toString();
					final JSONObject jsonObject = new JSONObject(secretString);
					System.out.println("user"+jsonObject.get("user"));
					System.out.println("password"+jsonObject.get("password"));

					final Properties properties = new Properties();
					properties.put("user", jsonObject.get("user"));
					properties.put("password", jsonObject.get("password"));
					//properties.put("db", jsonObject.get("db"));
					properties.put("role", jsonObject.get("role"));
					//properties.put("warehouse", jsonObject.get("warehouse"));
					Connection res = snowflake.createSnowConnection(properties);
					System.out.println("res"+res);
				}
			}
			apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
			apiResponse.put(ApplicationConstants.DATA, "Connection snowfalke created");
			return new ResponseEntity<>(apiResponse, HttpStatus.OK);
		}
}
