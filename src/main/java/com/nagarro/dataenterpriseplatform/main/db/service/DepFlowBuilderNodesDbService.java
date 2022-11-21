package com.nagarro.dataenterpriseplatform.main.db.service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderNodesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderNodesRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class DepFlowBuilderNodesDbService {

    @Autowired
    private DepFlowBuilderNodesRepo depFlowBuilderNodesRepo;

    public void createNewNode(DepFlowBuilderNodesEntity nodes) {
        try {
            DepFlowBuilderNodesEntity fetchedNodes = this.depFlowBuilderNodesRepo.findByClient_idAndBatch_idAndJobName(nodes.getClient_id(), nodes.getBatch_id(), nodes.getJobName());

            if (fetchedNodes != null) {
                this.depFlowBuilderNodesRepo.delete(fetchedNodes);
            }

            this.depFlowBuilderNodesRepo.save(nodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DepFlowBuilderNodesEntity getNodesByBatchAndJob(String client_id, String batch_id, String job) {
        DepFlowBuilderNodesEntity fetchedNodes = this.depFlowBuilderNodesRepo.findByClient_idAndBatch_idAndJobName(client_id, batch_id, job);
        return fetchedNodes;
    }

    public void deleteNodes(String client_id, String batch_id, String job) {
        DepFlowBuilderNodesEntity fetchedNodes = this.depFlowBuilderNodesRepo.findByClient_idAndBatch_idAndJobName(client_id, batch_id, job);

        if (fetchedNodes != null)
            this.depFlowBuilderNodesRepo.delete(fetchedNodes);
    }
}
