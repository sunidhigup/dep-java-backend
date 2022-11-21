package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderFormEntity;

@Repository
public interface DepFlowBuilderFormRepo extends JpaRepository<DepFlowBuilderFormEntity, String> {

    @Query(value = "Select * from dep_flow_builder_form u where u.client_id = ?1 and u.batch_id = ?2 and u.job_name = ?3", nativeQuery = true)
    public DepFlowBuilderFormEntity findByClient_idAndBatch_idAndJobname(String client_id, String batch_id,
                                                                         String jobname);
}
