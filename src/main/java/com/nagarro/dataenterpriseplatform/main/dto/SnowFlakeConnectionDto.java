package com.nagarro.dataenterpriseplatform.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnowFlakeConnectionDto {
	private String user;
	private String password;
	private String db;
	private String role;
	private String warehouse;
}
