package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;

@Repository
public interface DepFlowBuilderMetadataRepo extends JpaRepository<DepFlowBuilderMetadataEntity, String> {

    @Query(value = "Select * from dep_flowbuilder_metadata u where u.client_id = ?1 and u.batch_name = ?2", nativeQuery = true)
    public DepFlowBuilderMetadataEntity findByClient_idAndBatchName(String client_id, String batch_name);

    @Query(value = "Select * from dep_flowbuilder_metadata u where u.client_id = ?1", nativeQuery = true)
    public List<DepFlowBuilderMetadataEntity> findByClient_id(String client_id);

    @Query(value = "Select * from dep_flowbuilder_metadata u where u.batch_id = ?1", nativeQuery = true)
    public DepFlowBuilderMetadataEntity findByBatch_id(String batch_id);

    @Query(value = "Select * from dep_flowbuilder_metadata u where u.client_id = ?1 and u.batch_id = ?2", nativeQuery = true)
    public DepFlowBuilderMetadataEntity findByClient_idAndBatch_id(String client_id, String batch_id);

    @Query(value = "delete from dep_flowbuilder_metadata u where u.batch_id = ?1", nativeQuery = true)
    public DepFlowBuilderMetadataEntity deleteAllByBatch_id(String batch_id);
}
