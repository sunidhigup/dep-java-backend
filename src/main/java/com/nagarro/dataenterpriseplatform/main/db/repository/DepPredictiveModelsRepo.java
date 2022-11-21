package com.nagarro.dataenterpriseplatform.main.db.repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPredictiveModelsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepPredictiveModelsRepo extends JpaRepository<DepPredictiveModelsEntity,String> {
}
