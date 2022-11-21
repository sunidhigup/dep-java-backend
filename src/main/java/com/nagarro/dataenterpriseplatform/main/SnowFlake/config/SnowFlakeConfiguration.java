package com.nagarro.dataenterpriseplatform.main.SnowFlake.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeConfiguration {
	
	public static Connection connection;

	public Connection createSnowConnection(Properties properties) throws SQLException {		
		final String connectStr = "jdbc:snowflake://xl60174.ap-south-1.aws.snowflakecomputing.com/";
		connection = DriverManager.getConnection(connectStr, properties);
		return connection;

	}
}

