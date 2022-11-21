package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepGlobalRuleEntity;

@Repository
public interface CdepGlobalRuleRepo extends JpaRepository<CdepGlobalRuleEntity, String> {

    @Query(value = "select * from cdep_global_rule where rulename= ?1", nativeQuery = true)
    public CdepGlobalRuleEntity findByRulename(String rulename);
}
