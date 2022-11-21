package com.nagarro.dataenterpriseplatform.main.controller;

import com.nagarro.dataenterpriseplatform.main.db.entity.StatusBatchIdEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.StatusBatchIdDbService;
import com.nagarro.dataenterpriseplatform.main.service.BatchIdService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* Controller class for performing batch id operation for executing the latest step function
* */

@RestController
@RequestMapping("/api/batch_id")
@CrossOrigin(origins = "*")
public class BatchIdController {

    @Autowired
    private StatusBatchIdDbService statusBatchIdDbService;
    
    @Autowired
    private BatchIdService batchIdService;

    /*
    * API for creating batch id
    * */

    @PostMapping("/create")
    public ResponseEntity<?> createId(@RequestBody StatusBatchIdEntity table, HttpServletRequest request, HttpServletResponse response){
        this.statusBatchIdDbService.createId(table);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     * API for fetching batch id
     * */

    @GetMapping("/fetch/{batchId}")
    public ResponseEntity<?> fetchId(@PathVariable("batchId") String id, HttpServletRequest request, HttpServletResponse response){
    	final String fetchedId=  batchIdService.fetchId(id);
        if(fetchedId == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(fetchedId);
    }

    /*
     * API for fetching batch id with details
     * */

    @GetMapping("/fetchData/{batchId}")
    public ResponseEntity<?> fetchBatchIdData(@PathVariable("batchId") String id, HttpServletRequest request, HttpServletResponse response){
        StatusBatchIdEntity fetchedId = this.statusBatchIdDbService.getBatchIDDetails(id);

        if(fetchedId == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(fetchedId);
    }

}
