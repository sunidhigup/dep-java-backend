package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderEdgesEntity;

@Repository
public interface DepMdmFlowBuilderEdgesRepo extends JpaRepository<DepMdmFlowBuilderEdgesEntity, String> {

	@Query(value = "Select * from dep_mdm_flow_builder_edges where entity_name = ?1", nativeQuery = true)
	public List<DepMdmFlowBuilderEdgesEntity> findByEntityName(String entityName);
}
