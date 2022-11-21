package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepCustomRuleEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepCustomRuleDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepRulesDbService;
import com.nagarro.dataenterpriseplatform.main.dto.CustomRuleResponse;

@RestController
@RequestMapping("/api/custom-rule")
@CrossOrigin(origins = "*")
public class CustomRuleController {

	@Autowired
	private DepCustomRuleDbService depCustomRuleDbService;

	@Autowired
	private DepRulesDbService depRulesDbService;

	/*
	 * API for creating custom rule
	 */

	@PostMapping("/create-rule")
	public ResponseEntity<?> createCustomRule(@RequestBody DepCustomRuleEntity rule, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean ruleExist = this.depCustomRuleDbService.addCustomRule(rule);

			if (!ruleExist) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			}
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API for fetching custom rule by rule name
	 */

	@GetMapping("/get-rule-by-rulename/{client_id}/{batch_id}/{rulename}")
	public ResponseEntity<?> fetchRuleByRulename(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, @PathVariable("rulename") String rulename,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			List<DepCustomRuleEntity> result = this.depCustomRuleDbService
					.getRuleByClientIdAndBatchIdAndRulename(client_id, batch_id, rulename);

			if (result == null || result.size() == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			return ResponseEntity.status(HttpStatus.OK).body(result.get(0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API for fetching all custom rules by batch name
	 */

	@GetMapping("/get-rules/{client_id}/{batch_id}")
	public ResponseEntity<?> fetchRulesByBatchname(@PathVariable("client_id") String client_id,
			@PathVariable("batch_id") String batch_id, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<DepCustomRuleEntity> result = this.depCustomRuleDbService.getRuleByClientIdAndBatchId(client_id,
					batch_id);

			if (result == null || result.size() == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to update the custom rule
	 */

	@PutMapping("/update-rule")
	public ResponseEntity<?> updateRule(@RequestBody DepCustomRuleEntity rule, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			this.depCustomRuleDbService.updateCustomRule(rule);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to delete custom rule
	 */

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteRule(@PathVariable("id") String id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			this.depCustomRuleDbService.deleteCustomRuleById(id);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/get-by-client-id/{client_id}")
	public ResponseEntity<?> fetchByClientId(@PathVariable String client_id) {

		List<DepCustomRuleEntity> list = this.depCustomRuleDbService.getRuleByClientId(client_id);

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	// get all custom rule

	@GetMapping("/get-all-custom-rule")
	public ResponseEntity<?> fetchAllCustomeRule() {

		List<DepCustomRuleEntity> list = this.depCustomRuleDbService.getAllCustomRules();
		List<CustomRuleResponse> customList = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			DepCustomRuleEntity e = list.get(i);
			CustomRuleResponse rule = new CustomRuleResponse();
			rule.setArgkey(e.getArgkey());
			rule.setArgvalue(e.getArgvalue());
			rule.setBatch_id(e.getBatch_id());
			rule.setClient_id(e.getClient_id());
			rule.setId(e.getId());
			rule.setRulename(e.getRulename());
			rule.setType(e.getType());

			customList.add(rule);
		}

		customList = customList.stream().distinct().collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(customList);
	}

	/*
	 * API to fetch rule name by field type
	 */

	@GetMapping("/get-rule-by-type/{fieldType}")
	public ResponseEntity<?> fetchRuleByType(@PathVariable("fieldType") String fieldType, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			List<String> rulenames = this.depRulesDbService.getRulename(fieldType);

			if (rulenames.size() == 0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(rulenames);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to fetch args value by rulename
	 */

	@GetMapping("/get-args-by-rulename/{rulename}")
	public ResponseEntity<?> fetchArgsByRulename(@PathVariable("rulename") String rulename, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String argsValue = this.depRulesDbService.getArgs(rulename);

			if (argsValue == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(argsValue);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
