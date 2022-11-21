package com.nagarro.dataenterpriseplatform.main.controller;

import com.nagarro.dataenterpriseplatform.main.db.service.CdepGlobalRuleDbService;
import com.nagarro.dataenterpriseplatform.main.db.entity.CdepGlobalRuleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/global-rule")
@CrossOrigin(origins = "*")
public class GlobalRuleController {

    @Autowired
    private CdepGlobalRuleDbService cdepGlobalRuleDbService;

    /*
     * API for fetching global rules
     * */

    @GetMapping("/fetch-global-rule")
    public ResponseEntity<List<CdepGlobalRuleEntity>> fetchGlobalRule(HttpServletRequest request, HttpServletResponse response) {
        List<CdepGlobalRuleEntity> rules = this.cdepGlobalRuleDbService.getGlobalRule();

        if (rules.size() == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(rules);
    }

    /*
     * API to create global rules
     * */

    @PostMapping("/create-global-rule")
    public ResponseEntity<?> createGlobalRule(@RequestBody CdepGlobalRuleEntity rules, HttpServletRequest request, HttpServletResponse response) {
        boolean ruleExist = this.cdepGlobalRuleDbService.addGlobalRules(rules);

        if(!ruleExist){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /*
     * API to delete global rule
     * */

    @DeleteMapping("/delete-global-rule/{id}")
    public ResponseEntity<?> deleteGlobalRule(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        this.cdepGlobalRuleDbService.deleteGlobalRule(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
     * API to update the global rule
     * */

    @PutMapping("/update-rule")
    public ResponseEntity<?>  updateRule(@RequestBody CdepGlobalRuleEntity rule, HttpServletRequest request, HttpServletResponse response) {
        try{
            this.cdepGlobalRuleDbService.updateGlobalRule(rule);

            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API for fetching global rule by rule name
     * */

    @GetMapping("/get-rule-by-rulename/{rulename}")
    public ResponseEntity<?> fetchRuleByRulename( @PathVariable("rulename") String rulename, HttpServletRequest request, HttpServletResponse response) {
        try {
            CdepGlobalRuleEntity result = this.cdepGlobalRuleDbService.getRuleByRulename(rulename);

            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
