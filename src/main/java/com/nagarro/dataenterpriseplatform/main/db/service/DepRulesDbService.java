package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepRulesRepo;

@Service
public class DepRulesDbService {

	@Autowired
	DepRulesRepo depRulesRepo;

	public List<DepRulesEntity> getAllRule() {

		return (List<DepRulesEntity>) this.depRulesRepo.findAll();
	}

	public List<DepRulesEntity> getRuleByDataType(String dataType) {
		return this.depRulesRepo.findByDatatype(dataType);

	}

	public List<String> getRulename(String dataType) {
		List<DepRulesEntity> rule = this.getRuleByDataType(dataType);
		List<String> rulenames = new ArrayList<>();
		for (DepRulesEntity item : rule) {
			rulenames.add(item.getRule_name());
		}
		return rulenames;
	}

	public DepRulesEntity getRuleById(String rulename) {

		return this.depRulesRepo.findById(rulename).orElse(null);
	}

	public String getArgs(String rulename) {
		DepRulesEntity rule = this.getRuleById(rulename);
		return rule.getArgs();
	}

	public String getArgsByRulename(String rulename) {
		final DepRulesEntity rule = getRuleById(rulename);
		return rule.getArgs();
	}

	public List<String> getRuleByType(String fieldType) {
		final List<DepRulesEntity> rules = depRulesRepo.findByDatatype(fieldType);
		List<String> rulenames = new ArrayList<>();
		for (DepRulesEntity item : rules) {
			rulenames.add(item.getRule_name());
		}
		return rulenames;
	}

}
