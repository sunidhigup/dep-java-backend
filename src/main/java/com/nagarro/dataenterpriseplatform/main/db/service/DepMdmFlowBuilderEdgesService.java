package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderEdgesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepMdmFlowBuilderEdgesRepo;

@Service
public class DepMdmFlowBuilderEdgesService {

	@Autowired
	DepMdmFlowBuilderEdgesRepo depMdmFlowBuilderEdgesRepo;

	public void createNewMDMEdge(DepMdmFlowBuilderEdgesEntity edge) {

		List<DepMdmFlowBuilderEdgesEntity> oldEdge = this.depMdmFlowBuilderEdgesRepo
				.findByEntityName(edge.getEntityName());

		if (oldEdge.size() > 0) {
			this.depMdmFlowBuilderEdgesRepo.delete(oldEdge.get(0));
		}

		this.depMdmFlowBuilderEdgesRepo.save(edge);
	}

	public DepMdmFlowBuilderEdgesEntity fetchEdgesByEntityName(String entityName) {

		List<DepMdmFlowBuilderEdgesEntity> edges = this.depMdmFlowBuilderEdgesRepo.findByEntityName(entityName);

		if (edges.size() == 0)
			return null;

		return edges.get(0);
	}

	public void deleteMDMEdge(String entityName) {

		DepMdmFlowBuilderEdgesEntity edge = this.fetchEdgesByEntityName(entityName);

		if (edge != null)
			this.depMdmFlowBuilderEdgesRepo.delete(edge);
	}
}
