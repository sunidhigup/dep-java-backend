package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepManagementDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepManagementDetailsRepo;

@Service
@Transactional
public class DepManagementDetailsDbService {

	@Autowired
	private DepManagementDetailsRepo repo;

	@Autowired
	private DepClientDbService clientService;

	public List<DepManagementDetailsEntity> getManagementEmployees() {
		return repo.findAll();
	}

	public boolean approveManagementEmployee(DepManagementDetailsEntity emp, String userId) {
		final List<DepManagementDetailsEntity> response = repo.findByEmail(emp.getEmail());
		if (response.size() > 0) {
			return true;
		}
		emp.setStatus("approved");
		emp.setAdmin_id(userId);
		// emp.setAdmin_id("9c1f2b3c-bd14-4c9f-91b5-a7a5f61d0ec9");
		repo.save(emp);
		return false;

	}

	public void updateManagementEmployee(DepManagementDetailsEntity emp) {
		final List<DepManagementDetailsEntity> response = repo.findByEmail(emp.getEmail());
		if (response.size() > 0) {
			repo.deleteById(response.get(0).getId());
		}
		repo.save(emp);
	}

	public boolean deleteManagementEmployee(String id) {
		final Optional<DepManagementDetailsEntity> employee = repo.findById(id);
		if (employee != null) {
			repo.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public DepManagementDetailsEntity GetUserDetaiInfo(String username) {
		final List<DepManagementDetailsEntity> response = repo.findByusername(username);
		if (response.size() == 1) {
			return response.get(0);
		}
		return null;
	}

	public DepManagementDetailsEntity getDetailsById(String id) {
		return this.repo.findById(id).get();
	}

	public List<DepManagementDetailsEntity> findByusername(String username) {
		return repo.findByusername(username);
	}

	public List<DepManagementDetailsEntity> findUserByAdminId(String adminid) {
		@SuppressWarnings("unchecked")
		List<DepManagementDetailsEntity> userlist = this.repo.findByAdminId(adminid);
		return userlist;
	}

	public void saveUserDetails(DepManagementDetailsEntity userDetails) {
		this.repo.save(userDetails);
	}

	public void grantClientAccessToUsers(String clientId, List<String> userIds) {
		try {
			List<DepManagementDetailsEntity> users = userIds.stream().map((e) -> this.repo.findById(e).get())
					.collect(Collectors.toList());

			DepClientEntity client = this.clientService.getClientById(clientId);

			users.stream().forEach((e) -> {
				List<DepClientEntity> oldClients = e.getClient();
				oldClients.add(client);
				e.setClient(oldClients);
			});

			System.out.println(users);
		} catch (

		Exception ex) {
			ex.printStackTrace();
		}
	}

	public DepManagementDetailsEntity getDetailByEmail(String email) {
		List<DepManagementDetailsEntity> details = this.repo.findByemail(email);

		if (details.isEmpty())
			return null;

		return details.get(0);
	}

}
