package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepConnectionEntity;

public interface DepConnectionRepo extends JpaRepository<DepConnectionEntity, String> {

	@Query(value = "select * from dep_connection where connection_name =?1", nativeQuery = true)
	List<DepConnectionEntity> findByConnectionName(String name);

	@Query(value = "select * from dep_connection where user_name =?1 and connection_type=?2", nativeQuery = true)
	List<DepConnectionEntity> findByConnectionNameAndUserName(String name, String type);
}
