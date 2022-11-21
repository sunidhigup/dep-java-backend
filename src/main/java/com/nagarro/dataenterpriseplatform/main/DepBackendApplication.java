package com.nagarro.dataenterpriseplatform.main;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = { "com.nagarro.dataenterpriseplatform.main" })
@EnableAspectJAutoProxy
@ComponentScan({ "com.nagarro.dataenterpriseplatform.*" })
public class DepBackendApplication {

	@Autowired
	private Environment env;


	public static void main(String[] args) {
		SpringApplication.run(DepBackendApplication.class, args);
	}

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                String url = env.getProperty("cors.urls");
////                registry.addMapping("/api/**").allowedOrigins(url).allowCredentials(true);
//                registry.addMapping("/api/**").allowedOrigins(url);
//            }
//        };
//    }

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
