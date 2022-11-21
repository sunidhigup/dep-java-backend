package com.nagarro.dataenterpriseplatform.main.db.repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobInputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepFlowBuilderJobInputRepo extends JpaRepository<DepFlowBuilderJobInputEntity,String> {
    @Query(value = "Select * from dep_flowbuilder_job_input u where u.client_id = ?1 and u.batch_id = ?2 and u.job = ?3", nativeQuery = true)
    public DepFlowBuilderJobInputEntity findByClient_idAndBatch_idAndJob(String client_id, String batch_id, String job);
}
