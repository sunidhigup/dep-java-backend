package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepCustomRuleEntity;

@Repository
public interface DepCustomRuleRepo extends JpaRepository<DepCustomRuleEntity, String> {

	@Query(value = "select * from dep_custom_rule where client_id=?1 and batch_id=?2 and rulename=?3", nativeQuery = true)
	List<DepCustomRuleEntity> findByClient_idAndBatch_idAndRulename(String client_id, String batch_id, String rulename);

	@Query(value = "select * from dep_custom_rule where client_id=?1 and batch_id=?2", nativeQuery = true)
	List<DepCustomRuleEntity> findByClient_idAndBatch_id(String client_id, String batch_id);

	@Query(value = "select * from dep_custom_rule where client_id=?1", nativeQuery = true)
	List<DepCustomRuleEntity> findByClient_id(String client_id);

	@Query(value = "select * from dep_custom_rule where client_id=?1  and rulename=?2", nativeQuery = true)
	List<DepCustomRuleEntity> findByClient_idAndRulename(String client_id, String Rulename);

}
