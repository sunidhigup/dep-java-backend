package com.nagarro.dataenterpriseplatform.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderEdgesEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderNodesEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepMdmFlowBuilderEdgesService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepMdmFlowBuilderNodesService;
import com.nagarro.dataenterpriseplatform.main.utils.ValidateAwsCognitoJwtToken;

@RestController
@RequestMapping("/api/mdm")
public class MDMNodeEdgeController {

	@Autowired
	private DepMdmFlowBuilderNodesService nodeService;

	@Autowired
	private DepMdmFlowBuilderEdgesService edgeService;

	@Autowired
	private ValidateAwsCognitoJwtToken validateAwsCognitoJwtToken;

	@PostMapping("/add-nodes")
	public ResponseEntity<?> createNode(@RequestBody DepMdmFlowBuilderNodesEntity nodes, HttpServletRequest request,
			HttpServletResponse response) {

		try {

			this.nodeService.createNewMDMNode(nodes);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to geta all nodes data
	 */

	@GetMapping("/get-nodes/{entityName}")
	public ResponseEntity<?> fetchNodes(@PathVariable("entityName") String entityName, HttpServletRequest request,
			HttpServletResponse response) {

		DepMdmFlowBuilderNodesEntity node = this.nodeService.fetchMDMNodesByEntityName(entityName);
		if (node != null)
			return ResponseEntity.ok(node);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to delete flow builder nodes
	 */

	@DeleteMapping(value = "/delete-nodes/{entityName}")
	public ResponseEntity<?> deleteMDMNode(@PathVariable("entityName") String entityName, HttpServletRequest request,
			HttpServletResponse response) {
		this.nodeService.deleteNodeByEntityName(entityName);
		return ResponseEntity.ok().build();
	}

	/*
	 * API to create edges of nodes
	 */

	@PostMapping("/add-edges")
	public ResponseEntity<?> createEdge(@RequestBody DepMdmFlowBuilderEdgesEntity edges, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			this.edgeService.createNewMDMEdge(edges);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to fetch all edges data
	 */

	@GetMapping("/get-edges/{entityName}")
	public ResponseEntity<?> fetchEdges(@PathVariable("entityName") String entityName, HttpServletRequest request,
			HttpServletResponse response) {

		DepMdmFlowBuilderEdgesEntity edges = this.edgeService.fetchEdgesByEntityName(entityName);
		if (edges != null)
			return ResponseEntity.ok(edges);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/*
	 * API to delete flow builder edges
	 */

	@DeleteMapping(value = "/delete-edges/{entityName}")
	public ResponseEntity<?> deleteMDMEdge(@PathVariable("entityName") String entityName, HttpServletRequest request,
			HttpServletResponse response) {
		this.edgeService.deleteMDMEdge(entityName);
		return ResponseEntity.ok().build();
	}

}
