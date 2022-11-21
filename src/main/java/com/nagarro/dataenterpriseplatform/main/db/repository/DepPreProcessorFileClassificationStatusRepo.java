package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationStatusEntity;

@Repository
public interface DepPreProcessorFileClassificationStatusRepo extends JpaRepository<DepPreProcessorFileClassificationStatusEntity,String> {

    @Query(value = "SELECT * FROM dep_preprocessor_file_classification_status WHERE client_name = ?1 and batch_name = ?2 and table_name = ?3 ORDER BY timestamp_id DESC LIMIT 1", nativeQuery = true)
    public DepPreProcessorFileClassificationStatusEntity findByClientBatchTable(String client, String batch, String table);
}
