package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.nagarro.dataenterpriseplatform.main.db.entity.DepCustomRuleEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepCustomRuleRepo;

import javax.transaction.Transactional;

@Service
@Transactional
public class DepCustomRuleDbService {

	@Autowired
	private DepCustomRuleRepo depCustomRuleRepo;

	public boolean addCustomRule(DepCustomRuleEntity depCustomRuleEntity) {
		DepCustomRuleEntity rule = this.depCustomRuleRepo.save(depCustomRuleEntity);

		if (rule != null)
			return true;
		else
			return false;
	}

	public List<DepCustomRuleEntity> getRuleByClientId(String client_id) {

		return this.depCustomRuleRepo.findByClient_id(client_id);

	}

	public List<DepCustomRuleEntity> getRuleByClientIdAndBatchId(String client_id, String batch_id) {

		return this.depCustomRuleRepo.findByClient_idAndBatch_id(client_id, batch_id);

	}

	public List<DepCustomRuleEntity> getRuleByClientIdAndBatchIdAndRulename(String client_id, String batch_id,
                                                                            String rulename) {

		return this.depCustomRuleRepo.findByClient_idAndBatch_idAndRulename(client_id, batch_id, rulename);

	}

	public void updateCustomRule(DepCustomRuleEntity depCustomRuleEntity) {
		this.depCustomRuleRepo.save(depCustomRuleEntity);
	}

	public void deleteCustomRuleById(String id) {
		this.depCustomRuleRepo.deleteById(id);
	}

	public List<DepCustomRuleEntity> getAllCustomRules() {

		return this.depCustomRuleRepo.findAll();
	}
	
	public boolean createRule(DepCustomRuleEntity rule) {
		final List<DepCustomRuleEntity> fetchedRule  = depCustomRuleRepo.findByClient_idAndBatch_idAndRulename(rule.getClient_id(), rule.getBatch_id(), rule.getRulename());
		

		if (fetchedRule.size() != 0) {
			return true;
		}
		depCustomRuleRepo.save(rule);
		
		return false;
	}

	public DepCustomRuleEntity getRuleByRulename(String client_id, String batch_id, String rulename) {
		final List<DepCustomRuleEntity> fetchedRule  = depCustomRuleRepo.findByClient_idAndBatch_idAndRulename(client_id,batch_id, rulename);
		if (fetchedRule.size() == 0) {
			return null;
		}
		return fetchedRule.get(0);
	}

	public List<DepCustomRuleEntity> getCustomRule(String client_id, String batch_id) {
		final List<DepCustomRuleEntity> fetchedRule = depCustomRuleRepo.findByClient_idAndBatch_id(client_id, batch_id);
		if (fetchedRule.size() == 0) {
			return null;
		}
		return fetchedRule;
	}

	public void deleteCustomRule(String id) {
		final DepCustomRuleEntity rule = depCustomRuleRepo.findById(id).get();
		if (rule != null) {
			depCustomRuleRepo.delete(rule);
		}
	}

	public List<DepCustomRuleEntity> getCustomRuleByClient_id(String client_id) {
		return  depCustomRuleRepo.findByClient_id(client_id);
	}

}
