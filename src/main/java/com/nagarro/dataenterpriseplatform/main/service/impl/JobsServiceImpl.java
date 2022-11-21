package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobNameDbService;
import com.nagarro.dataenterpriseplatform.main.dto.FlowBuilderResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.BatchIdService;
import com.nagarro.dataenterpriseplatform.main.service.BatchService;
import com.nagarro.dataenterpriseplatform.main.service.JobsService;

@Service
public class JobsServiceImpl implements JobsService {

    @Autowired
    private DepFlowBuilderJobNameDbService depFlowBuilderJobNameDbService;

    @Autowired
    private BatchIdService batchIdService;

    @Autowired
    private BatchService batchService;

    public List<FlowBuilderResponseDto> getAllJobsANdStatus(String clientId, String batchId) {
        final List<FlowBuilderResponseDto> response = new ArrayList<>();

        try {
            final List<DepFlowBuilderJobNameEntity> jobNameList = depFlowBuilderJobNameDbService.getAllJobs(clientId,
                    batchId);
            for (DepFlowBuilderJobNameEntity jobs : jobNameList) {
                final String executionId = jobs.getClient_name() + "_" + jobs.getBatch_name() + "_"
                        + jobs.getInput_ref_key() + "_" + ApplicationConstants.DATA_PROCESSOR;
                final String statusBatchId = batchIdService.fetchId(executionId);
                if (statusBatchId != null && !statusBatchId.isEmpty()) {
                    final DepFlowbuilderJobStatusEntity jobStatus = batchService.getJobStatus(statusBatchId);
                    if (jobStatus != null) {
                        final FlowBuilderResponseDto setDtoResponse = setReponse(jobStatus.getStatus(), jobs, statusBatchId);
                        response.add(setDtoResponse);
                    } else {
                        final FlowBuilderResponseDto setDtoResponse = setReponse(null, jobs, statusBatchId);
                        response.add(setDtoResponse);
                    }
                } else {
                    final FlowBuilderResponseDto setDtoResponse = setReponse(ApplicationConstants.UNKNOWN, jobs, null);
                    response.add(setDtoResponse);
                }
            }
        } catch (Exception e) {
            System.out.println("eroor" + e);
        }
        return response;
    }

    private FlowBuilderResponseDto setReponse(String jobStatus, DepFlowBuilderJobNameEntity jobs, String executionId) {
        final FlowBuilderResponseDto dtoResponse = new FlowBuilderResponseDto();
        if (jobStatus != null && executionId != null) {
            dtoResponse.setJob_id(jobs.getJob_id());
            dtoResponse.setInput_ref_key(jobs.getInput_ref_key());
            dtoResponse.setConnectionName(jobs.getConnectionName());
            dtoResponse.setConnectionType(jobs.getConnectionType());
            dtoResponse.setJob_status(jobStatus);
            dtoResponse.setActive(jobs.is_active());
            dtoResponse.setExecution_id(executionId);
        } else {
            dtoResponse.setJob_id(jobs.getJob_id());
            dtoResponse.setInput_ref_key(jobs.getInput_ref_key());
            dtoResponse.setConnectionName(jobs.getConnectionName());
            dtoResponse.setConnectionType(jobs.getConnectionType());
            dtoResponse.setJob_status(ApplicationConstants.UNKNOWN);
            dtoResponse.setActive(jobs.is_active());
            dtoResponse.setExecution_id(executionId);
        }
        return dtoResponse;
    }

    @Override
    public FlowBuilderResponseDto getJobStatus(String jobId, String execution_id) {
        String status = null;
        String exec_id = null;
        FlowBuilderResponseDto response;

        final DepFlowBuilderJobNameEntity fetchedJob = depFlowBuilderJobNameDbService.getJobById(jobId);
        final String statusBatchId = batchIdService.fetchId(execution_id);

        if (statusBatchId != null && !statusBatchId.isEmpty()) {
            final DepFlowbuilderJobStatusEntity jobStatus = batchService.getJobStatus(statusBatchId);
            if(jobStatus != null)
                status = jobStatus.getStatus() != null ? jobStatus.getStatus() : null;
            exec_id = statusBatchId;
        } else {
            status = ApplicationConstants.UNKNOWN;
        }

        response = setReponse(status, fetchedJob, exec_id);
        return response;
    }
}
