package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepJsonPathEntity;

@Repository
public interface CdepJsonPathRepo extends JpaRepository<CdepJsonPathEntity, String>{
	
	@Query(value = "select * from cdep_json_path where  client_id =?1 AND source table_name =?2 AND batch_name=3", nativeQuery = true)
	List<CdepJsonPathEntity> findByClientIdTableAndBatch(String clientId,String tablename,String batchname);

}
