package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepRuleEngineJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepTableRulesDbService;
import com.nagarro.dataenterpriseplatform.main.dto.FlowBuilderResponseDto;
import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.BatchIdService;
import com.nagarro.dataenterpriseplatform.main.service.TableRuleService;

@Service
public class TableRuleServiceImpl implements TableRuleService {

    @Autowired
    private DepTableRulesDbService depTableRulesDbService;

    @Autowired
    private DepClientDbService depClientDbService;

    @Autowired
    private BatchIdService batchIdService;

    @Autowired
    private DepRuleEngineJobStatusDbService depRuleEngineJobStatusDbService;

    @Override
    public List<RuleEngineResponseDto> getTableRulesWithStatus(String clientId, String batchName) {
        final List<RuleEngineResponseDto> response = new ArrayList<>();
        final List<DepTableRulesEntity> Rulelist = depTableRulesDbService.fetchTableRule(clientId, batchName);
        if (Rulelist == null) {
            return null;
        }
        final DepClientEntity clientInfo = depClientDbService.getClientById(clientId);
        for (DepTableRulesEntity rule : Rulelist) {
            final String executionId = clientInfo.getClient_name() + "_" + rule.getBatchname() + "_"
                    + rule.getTablename() + "_" + ApplicationConstants.RULE_ENGINE;
            final String statusBatchId = batchIdService.fetchId(executionId);
            if (statusBatchId != null && !statusBatchId.isEmpty()) {
                DepRuleEngineJobStatusEntity status = depRuleEngineJobStatusDbService.getStatus(statusBatchId);
                if (status != null) {
                    final RuleEngineResponseDto setDtoResponse = setReponse(status.getStatus(), rule, statusBatchId);
                    response.add(setDtoResponse);
                } else {
                    final RuleEngineResponseDto setDtoResponse = setReponse(null, rule, statusBatchId);
                    response.add(setDtoResponse);
                }

            } else {
                final RuleEngineResponseDto setDtoResponse = setReponse(ApplicationConstants.UNKNOWN, rule, null);
                response.add(setDtoResponse);
            }
        }
        return response;
    }

    private RuleEngineResponseDto setReponse(String status, DepTableRulesEntity rule, String executionId) {
        final RuleEngineResponseDto dtoResponse = new RuleEngineResponseDto();
        if (status != null) {
            dtoResponse.setId(rule.getId());
            dtoResponse.setBatchname(rule.getBatchname());
            dtoResponse.setFields(rule.getFields());
            dtoResponse.setGenerated(rule.getGenerated());
            dtoResponse.setPath(rule.getPath());
            dtoResponse.setStatus(status);
            dtoResponse.setClient_id(rule.getClient_id());
            dtoResponse.setTablename(rule.getTablename());
            dtoResponse.setExecution_id(executionId);

        } else {
            dtoResponse.setId(rule.getId());
            dtoResponse.setBatchname(rule.getBatchname());
            dtoResponse.setFields(rule.getFields());
            dtoResponse.setGenerated(rule.getGenerated());
            dtoResponse.setPath(rule.getPath());
            dtoResponse.setClient_id(rule.getClient_id());
            dtoResponse.setTablename(rule.getTablename());
            dtoResponse.setStatus(ApplicationConstants.UNKNOWN);
            dtoResponse.setExecution_id(executionId);
        }
        return dtoResponse;
    }

    @Override
    public RuleEngineResponseDto getJobStatus(String jobId, String executionId) {
        String status = null;
        final DepTableRulesEntity Rulelist = depTableRulesDbService.fetchTableRuleById(jobId);
        final String statusBatchId = batchIdService.fetchId(executionId);
        if (statusBatchId != null && !statusBatchId.isEmpty()) {
            DepRuleEngineJobStatusEntity jobstatus = depRuleEngineJobStatusDbService.getStatus(statusBatchId);

            status = jobstatus.getStatus() != null ? jobstatus.getStatus() : ApplicationConstants.UNKNOWN;
        } else {
            status = ApplicationConstants.UNKNOWN;
        }
        return setReponse(status, Rulelist, statusBatchId);
    }

    @Override
    public RuleEngineResponseDto getTableRuleById(String jobId) {
        final DepTableRulesEntity rule = depTableRulesDbService.fetchTableRuleById(jobId);
        final DepClientEntity clientInfo = depClientDbService.getClientById(rule.getClient_id());
        final String executionId = clientInfo.getClient_name() + "_" + rule.getBatchname() + "_"
                + rule.getTablename() + "_" + ApplicationConstants.RULE_ENGINE;
        final String statusBatchId = batchIdService.fetchId(executionId);

        if (statusBatchId != null && !statusBatchId.isEmpty()) {
            DepRuleEngineJobStatusEntity status = depRuleEngineJobStatusDbService.getStatus(statusBatchId);
            if (status != null) {
                return setReponse(status.getStatus(), rule, statusBatchId);
            } else {
                return setReponse(null, rule, statusBatchId);
            }

        } else {
            return setReponse(ApplicationConstants.UNKNOWN, rule, null);

        }
    }

}
