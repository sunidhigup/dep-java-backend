package com.nagarro.dataenterpriseplatform.main.db.service;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepClientRuleEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.CdepClientRuleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CdepClientRuleDbService {

    @Autowired
    private CdepClientRuleRepo cdepClientRuleRepo;

    public boolean addClientRules(CdepClientRuleEntity rules) {
        try {
            CdepClientRuleEntity fetchClientRule = this.cdepClientRuleRepo.findByClient_idAndRulename(rules.getClient_id(), rules.getRulename());

            if (fetchClientRule != null) {
                return false;
            }

            this.cdepClientRuleRepo.save(rules);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CdepClientRuleEntity> getClientRule(String client_id) {
        System.out.println(client_id);
        List<CdepClientRuleEntity> fetchClientRule = this.cdepClientRuleRepo.findByClient_id(client_id);

        if (fetchClientRule.size() == 0) return null;

        return fetchClientRule;
    }

    public void deleteClientRule(String id) {
        CdepClientRuleEntity rule = this.cdepClientRuleRepo.findById(id).orElse(null);

        if (rule != null) {
            this.cdepClientRuleRepo.delete(rule);
        }
    }

    public void updateCustomRule(CdepClientRuleEntity rule) {
        CdepClientRuleEntity fetchClientRule = this.cdepClientRuleRepo.findByClient_idAndRulename(rule.getClient_id(), rule.getRulename());

        if (fetchClientRule != null) {
            this.cdepClientRuleRepo.delete(fetchClientRule);
        }

        this.cdepClientRuleRepo.save(rule);
    }

    public CdepClientRuleEntity getRuleByRulename(String client_id, String rulename) {

        CdepClientRuleEntity fetchClientRule = this.cdepClientRuleRepo.findByClient_idAndRulename(client_id, rulename);
        return fetchClientRule;

    }

}
