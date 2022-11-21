package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderNodesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepMdmFlowBuilderNodesRepo;

@Service
public class DepMdmFlowBuilderNodesService {

	@Autowired
	DepMdmFlowBuilderNodesRepo depMdmFlowBuilderNodesRepo;

	public void createNewMDMNode(DepMdmFlowBuilderNodesEntity node) {

		List<DepMdmFlowBuilderNodesEntity> entity = this.depMdmFlowBuilderNodesRepo
				.findByEntityName(node.getEntityName());

		if (entity.size() > 0)
			this.depMdmFlowBuilderNodesRepo.delete(entity.get(0));

		this.depMdmFlowBuilderNodesRepo.save(node);
	}

	public DepMdmFlowBuilderNodesEntity fetchMDMNodesByEntityName(String entityName) {
		List<DepMdmFlowBuilderNodesEntity> nodes = this.depMdmFlowBuilderNodesRepo.findByEntityName(entityName);

		if (nodes.size() > 0)
			return nodes.get(0);

		return null;
	}

	public void deleteNodeByEntityName(String entityName) {

		DepMdmFlowBuilderNodesEntity node = this.fetchMDMNodesByEntityName(entityName);

		if (node != null)
			this.depMdmFlowBuilderNodesRepo.delete(node);
	}
}
