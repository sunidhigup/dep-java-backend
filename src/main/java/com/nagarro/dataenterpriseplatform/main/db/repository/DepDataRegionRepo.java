package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepDataRegionEntity;

@Repository
public interface DepDataRegionRepo extends JpaRepository<DepDataRegionEntity, String> {

    @Query(value = "select * from dep_data_region where  data_region_code =?1", nativeQuery = true)
    DepDataRegionEntity findByCode(String code);

    @Query(value = "select * from dep_data_region where  id =?1", nativeQuery = true)
    DepDataRegionEntity findByDataRegionId(String id);

}
