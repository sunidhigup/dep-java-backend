package com.nagarro.dataenterpriseplatform.main.Security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nagarro.dataenterpriseplatform.main.constants.AuthConstants;
import com.nagarro.dataenterpriseplatform.main.filter.JwtFilter;
import com.nagarro.dataenterpriseplatform.main.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private JwtAuthenticationEntryPoint entryPoint;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userService);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf().disable().httpBasic().and().authorizeRequests()
				.antMatchers("/api/auth/authenticate", "/api/auth/register", "/api/auth/logout", "/**/health",
						"/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
						"/configuration/security", "/swagger-ui.html", "/webjars/**")
				.permitAll()
				.antMatchers(HttpMethod.POST, "/api/table-rule/get-s3-table-rules", "/api/table-rule/get-csv-data",
						"/api/table-rule/get-json-data", "/add-table-rule/add-s3-path",
						"/api/table-rule/table-rule-json/{client_id}/{batchname}/{tablename}/{idt}",
						"/api/table-rule/create-csv-to-json/{client_id}/{batchname}/{tablename}",
						"/api/table-rule/add-table-rule", "/api/table-rule/rule-engine-metadata",
						"/api/table-rule/get-status", "/api/table-rule/get-status-table", "/api/batch/add-batch",
						"/api/table-rule/get-status", "/api/client/**", "/api/jobs/add-job", "/api/jobs/copy-job",
						"/api/jobs/create-step-function/{batch}/{job}", "/api/stream/create-stream",
						"/api/stream/create-stream/new-data-stream", "/api/emr/create-emr-cluster",
						"/api/table-rule/upload-json", "/api/pre-processor/add-preprocess",
						"/api/global-rule/create-global-rule", "/api/custom-rule/create-rule",
						"/api/entity/create-entity", "/api/mdm/flow//store-json/{entityName}", "/api/mdm/add-form-data",
						"/api/mdm/add-nodes", "/api/mdm/add-edges", "/api/client-rule/create-client-rule",
						"/api/flow-builder-form//get-udf-names",
						"/api/flow-builder-form/convert-to-json/{client}/{batch}/{job}/{trackingId}/{timestamptype}",
						"/api/flow-builder-form/add-form-data",
						"/api/flow-builder-form/add-job-input/{client_id}/{batch_id}/{batch}/{job}",
						"/api/flow-builder-nodes/add-nodes", "/api/flow-builder-nodes/add-edges"

				).hasAnyAuthority(AuthConstants.ROLE_EXECUTOR, AuthConstants.ROLE_ADMIN)
				.antMatchers(HttpMethod.PUT, "/api/global-rule/update-rule", "/api/custom-rule/update-rule",
						"/api/client-rule/update-rule", "/api/jobs/update-job-detail", "/api/jobs/update-job-detail",
						"/api/jobs/disable/{client_id}/{batch_id}/{job}",
						"/api/jobs/enable/{client_id}/{batch_id}/{job}", "/api/jobs/update-extract-job",
						"api/jobs/update-job-run-type", "/api/pre-processor/disableEnablePreProcessorJob",
						"/api/pre-processor/update-preprocess")
				.hasAnyAuthority(AuthConstants.ROLE_EXECUTOR, AuthConstants.ROLE_ADMIN)
				.antMatchers(HttpMethod.DELETE, "/api/entity/delete-entity/{entityId}",
						"/api/mdm/delete-form-data/{entityName}", "/api/mdm/delete-nodes/{entityName}",
						"/api/mdm/delete-edges/{entityName}", "/api/client-rule/delete-client-rule/{id}",
						"/api/jobs/delete-job/{client_id}/{batch_id}/{job}",
						"/api/flow-builder-form/delete-form/{client_id}/{batch_id}/{jobname}",
						"/api/flow-builder-form/delete-job-input/{client_id}/{batch_id}/{jobname}",
						"/api/flow-builder-form/delete-json/{batch}/{jobname}",
						"/api/flow-builder-nodes/delete-edges/{client_id}/{batch_id}/{jobname}")
				.hasAnyAuthority(AuthConstants.ROLE_EXECUTOR, AuthConstants.ROLE_ADMIN)
				.antMatchers(HttpMethod.GET, "/api/pre-processor/execute-preprocess")
				.hasAnyAuthority(AuthConstants.ROLE_EXECUTOR, AuthConstants.ROLE_ADMIN)
				.antMatchers(HttpMethod.GET, "/api/**")
				.hasAnyAuthority(AuthConstants.ROLE_ADMIN, AuthConstants.ROLE_READER, AuthConstants.ROLE_EXECUTOR)
				.antMatchers("/api/**").hasAuthority(AuthConstants.ROLE_ADMIN).anyRequest().authenticated().and()
				.exceptionHandling().authenticationEntryPoint(entryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}