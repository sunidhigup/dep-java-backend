package com.nagarro.dataenterpriseplatform.main.service;

import java.util.List;
import java.util.Map;

import com.nagarro.dataenterpriseplatform.main.dto.FlowBuilderResponseDto;

public interface JobsService {

	/**
	 * 
	 * @param client_id
	 * @param batch_id
	 * @return
	 */
	List<FlowBuilderResponseDto> getAllJobsANdStatus(String client_id,String batch_id);
	
	/**
	 * 
	 * @param jobId
	 * @param execution_id
	 * @return
	 */
	FlowBuilderResponseDto getJobStatus(String jobId, String execution_id);
}
