package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderNodesEntity;

@Repository
public interface DepFlowBuilderNodesRepo extends JpaRepository<DepFlowBuilderNodesEntity, String> {

    @Query(value = "select * from dep_flow_builder_nodes where client_id=?1 and batch_id=?2 and job_name=?3", nativeQuery = true)
    public DepFlowBuilderNodesEntity findByClient_idAndBatch_idAndJobName(String client_id, String batch_id,
                                                                          String jobName);

}
