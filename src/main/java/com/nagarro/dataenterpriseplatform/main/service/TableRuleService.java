package com.nagarro.dataenterpriseplatform.main.service;

import java.util.List;

import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineResponseDto;

public interface TableRuleService {

	/**
	 * 
	 * @param clientId
	 * @param batchId
	 * @return
	 */
	List<RuleEngineResponseDto> getTableRulesWithStatus(String clientId,String batchId);

	/**
	 * 
	 * @param jobId
	 * @param executionId
	 * @return
	 */
	RuleEngineResponseDto getJobStatus(String jobId,String executionId);


	/**
	 *
	 * @param jobId
	 * @return
	 */
	RuleEngineResponseDto getTableRuleById(String jobId);
}
