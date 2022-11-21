package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationStatusEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationStatusDbService;
import com.nagarro.dataenterpriseplatform.main.dto.PreProcessorResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.PreProcessorService;

@Service
public class PreProcessorServiceImpl implements PreProcessorService {

    @Autowired
    private DepPreProcessorFileClassificationDbService depPreProcessorFileClassificationDbService;

    @Autowired
    private DepPreProcessorFileClassificationStatusDbService depPreProcessorFileClassificationStatusDbService;

    @Override
    public List<PreProcessorResponseDto> getAllPreprocessorJobsWithStatus(String clientName, String batchName) {
        final List<PreProcessorResponseDto> listDtoObject = new ArrayList<>();
        final List<DepPreProcessorFileClassificationEntity> preResponse = depPreProcessorFileClassificationDbService
                .getPreProcessorClientBatch(clientName, batchName);

        if(preResponse == null) return null;

        for (DepPreProcessorFileClassificationEntity entity : preResponse) {
            final DepPreProcessorFileClassificationStatusEntity statusRes = depPreProcessorFileClassificationStatusDbService
                    .getPreprocessorStatus(entity.getClient_name(), entity.getBatch_name(), entity.getTable_name());
            final PreProcessorResponseDto dto = new PreProcessorResponseDto();
            dto.setId(entity.getId());
            dto.setClient_name(entity.getClient_name());
            dto.setBatch_name(entity.getBatch_name());
            dto.setDisableJob(entity.getDisableJob());
            dto.setExtension(entity.getExtension());
            dto.setStatus(statusRes != null ? statusRes.getStatus() : "Unknown");
            dto.setExecution_id(statusRes != null ? statusRes.getId() : null);
            dto.setTable_name(entity.getTable_name());
            dto.setKey(entity.getKey());
            dto.setFileDestination(entity.getFileDestination());
            dto.setPattern(entity.getPattern());
            dto.setSkip_PreProcess(entity.getSkip_PreProcess());
            listDtoObject.add(dto);
        }
        return listDtoObject;
    }

}
