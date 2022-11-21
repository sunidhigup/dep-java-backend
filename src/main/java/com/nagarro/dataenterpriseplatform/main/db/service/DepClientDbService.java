package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepDataRegionEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepManagementDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepClientRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobNameRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderMetadataRepo;

@Service
@Transactional
public class DepClientDbService {

	@Autowired
	private DepClientRepo depClientRepo;

	@Autowired
	private DepManagementDetailsDbService depManagementDetailsService;

	@Autowired
	private DepDataRegionService dataRegionService;

	@Autowired
	private DepFlowBuilderJobNameRepo depFlowBuilderJobNameRepo;

	@Autowired
	private DepFlowBuilderMetadataRepo depFlowBuilderMetadataRepo;

	public boolean saveClient(DepClientEntity client) {

		List<DepClientEntity> savedClient = depClientRepo.findByClient_name(client.getClient_name());

		if (savedClient.size() > 0)
			return true;

		String code = client.getData_region();
		DepDataRegionEntity entity = dataRegionService.getDataRegionByRegionCode(code);

		client.setDataRegionEntity(entity);
		depClientRepo.save(client);
		return false;

	}

	public List<DepClientEntity> getAllClient() {

		List<DepClientEntity> clients = depClientRepo.findAll();
		// System.out.println(clients);

		return clients;
	}

	public DepClientEntity getClientById(String client_id) {
		return depClientRepo.findById(client_id).orElse(null);

	}

	public List<DepClientEntity> getClientByName(String client_name) {
		return depClientRepo.findByClient_name(client_name);
	}

	public boolean deleteClientById(String client_id) {

		List<DepFlowBuilderJobNameEntity> jobs = this.depFlowBuilderJobNameRepo.findByClient_id(client_id);

		List<DepFlowBuilderMetadataEntity> batchs = this.depFlowBuilderMetadataRepo.findByClient_id(client_id);

		jobs.stream().forEach((e) -> this.depFlowBuilderJobNameRepo.delete(e));
		batchs.stream().forEach((e) -> this.depFlowBuilderMetadataRepo.delete(e));
		depClientRepo.deleteById(client_id);

		if (depClientRepo.findById(client_id) != null)
			return true;
		else
			return false;
	}

	public void updateClient(DepClientEntity depClientEntity) {
		depClientRepo.save(depClientEntity);
	}

	public List<DepClientEntity> getClientByApprovedStatus() {

		List<DepClientEntity> client = depClientRepo.findByStatus("approved");

		return client;
	}

	public List<DepClientEntity> getClientByApprovedStatus(String userId) {

		List<DepClientEntity> client = depClientRepo.findByUserIdAndStatus(userId, "approved");

		return client;
	}

	public List<DepClientEntity> getApprovedClient() {
		return depClientRepo.findByStatus("approved");
	}

	public List<DepClientEntity> getClientByUsername(String username) {

		List<DepManagementDetailsEntity> user = this.depManagementDetailsService.findByusername(username);
		String userId = "";
		if (user.size() > 0)
			userId = user.get(0).getId();

		return depClientRepo.findByUserid(userId);
	}

	public List<DepClientEntity> getClientByUserId(String userId) {
		List<DepClientEntity> client = this.depClientRepo.findByUserid(userId);
		return client;
	}

	public boolean saveClientForUser(String id, DepClientEntity depClientEntity) {

		try {

			DepManagementDetailsEntity user = this.depManagementDetailsService.getDetailsById(id);
			depClientEntity.setUser(user);

			DepDataRegionEntity dataRegion = this.dataRegionService
					.getDataRegionByRegionCode(depClientEntity.getData_region());

			// depClientEntity.setBucket_name(dataRegion.getBucket_name());

			DepClientEntity client = this.depClientRepo.save(depClientEntity);

			if (client != null)
				return true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public List<DepClientEntity> getClientByAdminId(String adminId) {

		List<DepManagementDetailsEntity> users = this.depManagementDetailsService.findUserByAdminId(adminId);

		List<DepClientEntity> clients = users.stream().flatMap(e -> e.getClient().stream())
				.collect(Collectors.toList());

		return clients;
	}

	public void updateClientForUser(DepClientEntity depClientEntity, String id) {

		DepManagementDetailsEntity user = this.depManagementDetailsService.getDetailsById(id);

		depClientEntity.setUser(user);

		this.saveClient(depClientEntity);
	}

	public List<DepClientEntity> findClientByUserId(String userId) {
		return depClientRepo.findByUserId(userId);
	}

	public DepClientEntity approveClientByUserEmail(String email, String clientid) {
		DepClientEntity savedclient = depClientRepo.findById(clientid).get();
		DepManagementDetailsEntity saveduser = depManagementDetailsService.getDetailByEmail(email);
		savedclient.setUser(saveduser);

		savedclient = this.depClientRepo.save(savedclient);
		return savedclient;

	}

	public DepClientEntity getClientByNameAndUserId(String clientName, String userId) {
		return this.depClientRepo.findByClientNameAndUserId(clientName, userId).get(0);
	}

	public DepClientEntity getDepClientByClientIdAndUserId(String client_id, String user_id) {
		List<DepClientEntity> client = depClientRepo.findByClientIdAndUserId(client_id, user_id);
		return client.get(0);

	}

	public DepClientEntity getDepClientByClientNameAndUserId(String client_name, String user_id) {
		return depClientRepo.findByClientIdAndUserId(client_name, user_id).get(0);

	}
}
