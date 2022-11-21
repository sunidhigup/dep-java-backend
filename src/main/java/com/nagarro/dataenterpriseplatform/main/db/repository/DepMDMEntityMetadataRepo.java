package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMDMEntityMetadataEntity;

@Repository
public interface DepMDMEntityMetadataRepo extends JpaRepository<DepMDMEntityMetadataEntity, String> {

	@Query(value = "select * from dep_MDM_entity_metadata where entity_name= ?1", nativeQuery = true)
	List<DepMDMEntityMetadataEntity> findByEntityName(String entityName);
}
