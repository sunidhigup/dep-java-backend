package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;

@Repository
public interface DepTableRulesRepo extends JpaRepository<DepTableRulesEntity, String> {

	@Query(value = "select * from dep_table_rules where client_id =?1 AND tablename =?2 AND batchname=?3", nativeQuery = true)
	List<DepTableRulesEntity> findByClientIdTableAndBatch(String clientId, String tablename, String batchname);

	@Query(value = "select * from dep_table_rules where client_id =?1 AND batchname=?2", nativeQuery = true)
	List<DepTableRulesEntity> findByClientIdContainsAndBatchname(String clientId, String batchname);

}
