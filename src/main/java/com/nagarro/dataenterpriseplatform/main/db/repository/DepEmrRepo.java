package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepEmrEntity;

@Repository
public interface DepEmrRepo extends JpaRepository<DepEmrEntity, String> {

}
