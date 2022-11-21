package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	String username;
	String password;
	String email;
	String confirmationCode;
	String refreshToken;
	String accessToken;
}
