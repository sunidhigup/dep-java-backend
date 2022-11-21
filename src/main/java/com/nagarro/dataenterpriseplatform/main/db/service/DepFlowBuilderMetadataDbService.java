package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobNameRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderMetadataRepo;

@Service
@Transactional
public class DepFlowBuilderMetadataDbService {

    @Autowired
    private DepFlowBuilderMetadataRepo depFlowBuilderMetadataRepo;

    @Autowired
    private DepFlowBuilderJobNameRepo depFlowBuilderJobNameRepo;

    @Value("${aws.dataProcessor.logGroupName}")
    private String logGroupName;

    public boolean addBatch(DepFlowBuilderMetadataEntity batch) {

        DepFlowBuilderMetadataEntity batchExist = this.depFlowBuilderMetadataRepo
                .findByClient_idAndBatchName(batch.getClient_id(), batch.getBatch_name());

        if (batchExist != null)
            return true;

        String logGroup = logGroupName;
        batch.setLog_group(logGroup);
        this.depFlowBuilderMetadataRepo.save(batch);

        return false;
    }

    public List<DepFlowBuilderMetadataEntity> getAllBatch(String client_id) {
        return this.depFlowBuilderMetadataRepo.findByClient_id(client_id);
    }

    public DepFlowBuilderMetadataEntity getBatchbyId(String batch_id) {
        return this.depFlowBuilderMetadataRepo.findByBatch_id(batch_id);
    }

    public boolean UpdateBatch(DepFlowBuilderMetadataEntity batch) {
        try {
            DepFlowBuilderMetadataEntity batchExist = this.depFlowBuilderMetadataRepo
                    .findByClient_idAndBatch_id(batch.getClient_id(), batch.getBatch_id());

            if (batchExist != null) {
                this.depFlowBuilderMetadataRepo.delete(batchExist);
            }

            this.depFlowBuilderMetadataRepo.save(batch);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean DeleteBatch(String batch_id) {
        try {
            this.depFlowBuilderMetadataRepo.deleteAllByBatch_id(batch_id);
            this.depFlowBuilderJobNameRepo.deleteAllByBatch_id(batch_id);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DepFlowBuilderMetadataEntity> getApprovedBatch(String client_id) {

        List<DepFlowBuilderMetadataEntity> all_batches = this.depFlowBuilderMetadataRepo.findByClient_id(client_id);
        List<DepFlowBuilderMetadataEntity> approvedBatches = new ArrayList<>();
        for (DepFlowBuilderMetadataEntity batch : all_batches) {
            if (batch.getStatus().equals("approved")) {
                approvedBatches.add(batch);
            }
        }

        return approvedBatches;
    }
    
    public List<DepFlowBuilderMetadataEntity> getAllBatches() {
        return this.depFlowBuilderMetadataRepo.findAll();
    }
}
