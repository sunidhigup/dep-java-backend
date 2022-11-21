package com.nagarro.dataenterpriseplatform.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderFormEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepMdmFlowBuilderFormService;

@RestController
@RequestMapping("/api/mdm")
public class MDMFlowBuilderFormController {

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.flowBuilderJsonLocation}")
	private String awsFlowBuilderJsonLocation;

	@Autowired
	private DepMdmFlowBuilderFormService formService;

	@GetMapping(value = "/get-form-data/{entityName}")
	public ResponseEntity<DepMdmFlowBuilderFormEntity> fetchMDMJobForm(@PathVariable("entityName") String entityName,
			HttpServletRequest request, HttpServletResponse response) {
		DepMdmFlowBuilderFormEntity list = this.formService.getMDMFormByEntityName(entityName);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(list);
	}

	@PostMapping(value = "/add-form-data")
	public ResponseEntity<DepMdmFlowBuilderFormEntity> addMDMFormData(@RequestBody DepMdmFlowBuilderFormEntity form,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			this.formService.createNewMDMFormData(form);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API delete flow builder form data
	 */

	@DeleteMapping(value = "/delete-form-data/{entityName}")
	public ResponseEntity<?> deleteForm(@PathVariable("entityName") String entityName, HttpServletRequest request,
			HttpServletResponse response) {
		this.formService.deleteMDMFormByEntityName(entityName);
		return ResponseEntity.ok().build();
	}

}
