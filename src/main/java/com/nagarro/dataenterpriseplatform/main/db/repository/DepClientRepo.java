package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;

@Repository
public interface DepClientRepo extends JpaRepository<DepClientEntity, String> {

	@Query(value = "select * from dep_client where  client_name =?1", nativeQuery = true)
	List<DepClientEntity> findByClient_name(String client_name);

	@Query(value = "select * from dep_client where  status =?1", nativeQuery = true)
	List<DepClientEntity> findByStatus(String status);

	@Query(value = "select * FROM dep_client  LEFT JOIN  dep_management_details on dep_client.user_id = dep_management_details.id WHERE dep_client.user_id = ?1", nativeQuery = true)
	List<DepClientEntity> findByUserid(String userId);

	@Query(value = "select * from dep_client where  user_id=?1 and status =?2", nativeQuery = true)
	List<DepClientEntity> findByUserIdAndStatus(String userId, String status);

	@Query(value = "select * from dep_client where  user_id=?1", nativeQuery = true)
	List<DepClientEntity> findByUserId(String userId);

	@Query(value = "select * from dep_client where client_name=?1 &&  user_id=?2", nativeQuery = true)
	List<DepClientEntity> findByClientNameAndUserId(String client_name, String userId);

	@Query(value = "select * from dep_client where  client_id=?1 and user_id=?2", nativeQuery = true)
	List<DepClientEntity> findByClientIdAndUserId(String client_id, String user_id);

	@Query(value = "select * from dep_client where  data_region_fk=?1 ", nativeQuery = true)
	List<DepClientEntity> findByDataRegionId(String id);

}
