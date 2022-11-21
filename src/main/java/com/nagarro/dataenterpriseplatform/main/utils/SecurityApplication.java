// package com.nagarro.dataenterpriseplatform.main.utils;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.NoOpPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.nagarro.dataenterpriseplatform.main.service.UserService;



// @SpringBootApplication
// public class SecurityApplication {

// 	@Autowired
//     private UserService userService;

// 	public static void main(String[] args) {
// 		SpringApplication.run(SecurityApplication.class, args);
// 	}

// 	// @Bean
// 	// public PasswordEncoder passwordEncoder() {
// 	// 	return NoOpPasswordEncoder.getInstance();
// 	// }

// 	@Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

// 	@Autowired
//     public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//         authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
//     }
// }
// 