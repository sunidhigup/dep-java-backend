package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepRulesEntity;

@Repository
public interface DepRulesRepo extends JpaRepository<DepRulesEntity, String> {

    @Query(value = "select * from dep_rules where datatype=?1", nativeQuery = true)
    List<DepRulesEntity> findByDatatype(String dataType);
}
