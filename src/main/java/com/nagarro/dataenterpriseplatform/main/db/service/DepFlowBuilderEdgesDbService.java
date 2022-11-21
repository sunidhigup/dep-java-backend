package com.nagarro.dataenterpriseplatform.main.db.service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderEdgesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderEdgesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class DepFlowBuilderEdgesDbService {

    @Autowired
    private DepFlowBuilderEdgesRepo depFlowBuilderEdgesRepo;

    public void createNewEdge(DepFlowBuilderEdgesEntity edges) {
        try {
            DepFlowBuilderEdgesEntity fetchedEdge = this.depFlowBuilderEdgesRepo.findByClient_idAndBatch_idAndJobName(edges.getClient_id(), edges.getBatch_id(), edges.getJobName());

            if (fetchedEdge != null) {
                this.depFlowBuilderEdgesRepo.delete(fetchedEdge);
            }

            this.depFlowBuilderEdgesRepo.save(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DepFlowBuilderEdgesEntity getEdgesByBatchAndJob(String client_id, String batch_id, String job) {
        DepFlowBuilderEdgesEntity fetchedEdge = this.depFlowBuilderEdgesRepo.findByClient_idAndBatch_idAndJobName(client_id,batch_id,job);
        return fetchedEdge;
    }

    public void deleteEdges(String client_id, String batch_id, String job) {
        DepFlowBuilderEdgesEntity fetchedEdge = this.depFlowBuilderEdgesRepo.findByClient_idAndBatch_idAndJobName(client_id,batch_id,job);

        if (fetchedEdge != null)
            this.depFlowBuilderEdgesRepo.delete(fetchedEdge);
    }
}
