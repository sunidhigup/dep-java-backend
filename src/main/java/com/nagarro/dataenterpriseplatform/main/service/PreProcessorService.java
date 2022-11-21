package com.nagarro.dataenterpriseplatform.main.service;

import java.util.List;

import com.nagarro.dataenterpriseplatform.main.dto.PreProcessorResponseDto;

public interface PreProcessorService {

	public List<PreProcessorResponseDto> getAllPreprocessorJobsWithStatus(String clientId,String batchId);
}
