package com.nagarro.dataenterpriseplatform.main.db.service;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepGlobalRuleEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.CdepGlobalRuleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class CdepGlobalRuleDbService {

    @Autowired
    private CdepGlobalRuleRepo cdepGlobalRuleRepo;

    public boolean addGlobalRules(CdepGlobalRuleEntity rule) {
        try {
            CdepGlobalRuleEntity fetchedRule = this.cdepGlobalRuleRepo.findByRulename(rule.getRulename());

            if(fetchedRule != null){
                return true;
            }

            this.cdepGlobalRuleRepo.save(rule);

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CdepGlobalRuleEntity> getGlobalRule() {
        return this.cdepGlobalRuleRepo.findAll();
    }

    public boolean deleteGlobalRule(String id) {
        CdepGlobalRuleEntity fetchedRule = this.cdepGlobalRuleRepo.findById(id).orElse(null);

        if (fetchedRule != null) {
            this.cdepGlobalRuleRepo.delete(fetchedRule);
            return true;
        } else {
            return false;
        }
    }

    public void updateGlobalRule(CdepGlobalRuleEntity rule) {
        CdepGlobalRuleEntity fetchedRule =  this.cdepGlobalRuleRepo.findByRulename(rule.getRulename());

        if(fetchedRule != null){
            this.cdepGlobalRuleRepo.delete(fetchedRule);
        }

        this.cdepGlobalRuleRepo.save(rule);
    }

    public CdepGlobalRuleEntity getRuleByRulename(String rulename) {

        CdepGlobalRuleEntity fetchedRule =  this.cdepGlobalRuleRepo.findByRulename(rulename);
        return fetchedRule;
    }
}
