package com.nagarro.dataenterpriseplatform.main.db.repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderEdgesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepFlowBuilderEdgesRepo extends JpaRepository<DepFlowBuilderEdgesEntity, String> {

    @Query(value = "Select * from dep_flow_builder_edges u where u.client_id = ?1 and u.batch_id = ?2 and u.job_name = ?3", nativeQuery = true)
    public DepFlowBuilderEdgesEntity findByClient_idAndBatch_idAndJobName(String client_id, String batch_id, String job);


}
