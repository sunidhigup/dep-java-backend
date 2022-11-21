package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepClientRuleEntity;

@Repository
public interface CdepClientRuleRepo extends JpaRepository<CdepClientRuleEntity, String> {

    @Query(value = "select * from cdep_client_rule where client_id=?1 and rulename= ?2", nativeQuery = true)
    public CdepClientRuleEntity findByClient_idAndRulename(String client_id, String rulename);

    @Query(value = "select * from cdep_client_rule where client_id=?1", nativeQuery = true)
    public List<CdepClientRuleEntity> findByClient_id(String client_id);
}
