package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepManagementDetailsEntity;

@Repository
public interface DepManagementDetailsRepo extends JpaRepository<DepManagementDetailsEntity, String> {

	@Query(value = "Select * from dep_management_details where email = ?1", nativeQuery = true)
	public List<DepManagementDetailsEntity> findByEmail(String email);

	@Query(value = "Select * from dep_management_details where username = ?1", nativeQuery = true)
	public List<DepManagementDetailsEntity> findByusername(String userName);

	@Query(value = "Select * from dep_management_details where admin_id = ?1", nativeQuery = true)
	public List<DepManagementDetailsEntity> findByAdminId(String adminid);

	@Query(value = "Select * from dep_management_details where email = ?1", nativeQuery = true)
	public List<DepManagementDetailsEntity> findByemail(String email);

}
