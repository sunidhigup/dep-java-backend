package com.nagarro.dataenterpriseplatform.main.controller;

import com.nagarro.dataenterpriseplatform.main.db.service.CdepClientRuleDbService;
import com.nagarro.dataenterpriseplatform.main.db.entity.CdepClientRuleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/client-rule")
@CrossOrigin(origins = "*")
public class ClientRuleController {

    @Autowired
    private CdepClientRuleDbService cdepClientRuleDbService;


    /*
     * API to fetch client rules
     * */

    @GetMapping("/fetch-client-rule/{client_id}")
    public ResponseEntity<List<CdepClientRuleEntity>> fetchClientRule(@PathVariable("client_id") String client_id, HttpServletRequest request, HttpServletResponse response) {
        List<CdepClientRuleEntity> rules = this.cdepClientRuleDbService.getClientRule(client_id);

        if (rules == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(rules);
    }

    /*
     * API to create client rule
     * */

    @PostMapping("/create-client-rule")
    public ResponseEntity<?> createClientRule(@RequestBody CdepClientRuleEntity rules, HttpServletRequest request, HttpServletResponse response) {
        boolean added = this.cdepClientRuleDbService.addClientRules(rules);

        if (added) {
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
    }

    /*
     * API to delete client rule
     * */

    @DeleteMapping("/delete-client-rule/{id}")
    public ResponseEntity<?> deleteClientRule(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        this.cdepClientRuleDbService.deleteClientRule(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
     * API to update the client rule
     * */

    @PutMapping("/update-rule")
    public ResponseEntity<?> updateRule(@RequestBody CdepClientRuleEntity rule, HttpServletRequest request, HttpServletResponse response) {
        try {
            this.cdepClientRuleDbService.updateCustomRule(rule);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API for fetching client rule by rule name
     * */

    @GetMapping("/get-rule-by-rulename/{client_id}/{rulename}")
    public ResponseEntity<?> fetchRuleByRulename(@PathVariable("client_id") String client_id, @PathVariable("rulename") String rulename, HttpServletRequest request, HttpServletResponse response) {
        try {
            CdepClientRuleEntity result = this.cdepClientRuleDbService.getRuleByRulename(client_id, rulename);

            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
