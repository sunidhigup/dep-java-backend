package com.nagarro.dataenterpriseplatform.main.controller;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderEdgesEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderNodesEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderEdgesDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderNodesDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Controller class for performing operations on flow builder nodes data
 * */

@RestController
@RequestMapping("/api/flow-builder-nodes")
@CrossOrigin(origins = "*")
public class FlowBuilderNodesController {

    @Autowired
    private DepFlowBuilderNodesDbService depFlowBuilderNodesDbService;

    @Autowired
    private DepFlowBuilderEdgesDbService depFlowBuilderEdgesDbService;

    /*
     * API to create nodes data
     * */

    @PostMapping("/add-nodes")
    public ResponseEntity<DepFlowBuilderNodesEntity> createNode(@RequestBody DepFlowBuilderNodesEntity nodes, HttpServletRequest request, HttpServletResponse response) {

        try {
            this.depFlowBuilderNodesDbService.createNewNode(nodes);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to geta all nodes data
     * */

    @GetMapping("/get-nodes/{client_id}/{batch_id}/{job}")
    public ResponseEntity<DepFlowBuilderNodesEntity> fetchNodes(@PathVariable("client_id") String client_id, @PathVariable("batch_id") String batch_id, @PathVariable("job") String job, HttpServletRequest request, HttpServletResponse response) {

        DepFlowBuilderNodesEntity nodes = this.depFlowBuilderNodesDbService.getNodesByBatchAndJob(client_id, batch_id, job);
        if (nodes != null) return ResponseEntity.ok(nodes);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /*
    * API to delete flow builder nodes
    * */

    @DeleteMapping(value = "/delete-nodes/{client_id}/{batch_id}/{jobname}")
    public ResponseEntity<DepFlowBuilderNodesEntity> deleteFlowBuilderNodes(@PathVariable("client_id") String client_id, @PathVariable("batch_id") String batch_id, @PathVariable("jobname") String job, HttpServletRequest request, HttpServletResponse response) {
        this.depFlowBuilderNodesDbService.deleteNodes(client_id, batch_id, job);
        return ResponseEntity.ok().build();
    }

    /*
     * API to create edges of nodes
     * */

    @PostMapping("/add-edges")
    public ResponseEntity<DepFlowBuilderEdgesEntity> createEdge(@RequestBody DepFlowBuilderEdgesEntity edges, HttpServletRequest request, HttpServletResponse response) {
        try {
            this.depFlowBuilderEdgesDbService.createNewEdge(edges);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to fetch all edges data
     * */

    @GetMapping("/get-edges/{client_id}/{batch_id}/{job}")
    public ResponseEntity<DepFlowBuilderEdgesEntity> fetchEdges(@PathVariable("client_id") String client_id, @PathVariable("batch_id") String batch_id, @PathVariable("job") String job, HttpServletRequest request, HttpServletResponse response) {

        DepFlowBuilderEdgesEntity edges = this.depFlowBuilderEdgesDbService.getEdgesByBatchAndJob(client_id, batch_id, job);
        if (edges != null) return ResponseEntity.ok(edges);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /*
    * API to delete flow builder edges
    * */

    @DeleteMapping(value = "/delete-edges/{client_id}/{batch_id}/{jobname}")
    public ResponseEntity<DepFlowBuilderEdgesEntity> deleteFlowBuilderEdges(@PathVariable("client_id") String client_id, @PathVariable("batch_id") String batch_id, @PathVariable("jobname") String job, HttpServletRequest request, HttpServletResponse response) {
        this.depFlowBuilderEdgesDbService.deleteEdges(client_id, batch_id, job);
        return ResponseEntity.ok().build();
    }
}
