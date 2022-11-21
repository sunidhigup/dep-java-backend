package com.nagarro.dataenterpriseplatform.main.db.repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.StatusBatchIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusBatchIdRepo extends JpaRepository<StatusBatchIdEntity,String> {

	@Query(value = "select * from status_batch_id where client_job =?1", nativeQuery = true)
    public StatusBatchIdEntity findByClient_job(String client_job);
}
