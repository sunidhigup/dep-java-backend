package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepDataRegionService;
import com.nagarro.dataenterpriseplatform.main.dto.DataRegionDto;

@RestController
@RequestMapping("/api/dataRegion")
@CrossOrigin(origins = "*")
public class DataRegionController {

    @Autowired
    DepDataRegionService depdDataRegionService;

    @GetMapping("/get-clients-by-data-region/{id}")
    public ResponseEntity<?> getClientDR(@PathVariable String id, HttpServletRequest request) {

        List<DepClientEntity> list = this.depdDataRegionService.getClientsDataRegionById(id);

        if (list == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/get-data-region")
    public ResponseEntity<?> getDataRegion(@RequestParam String id, HttpServletRequest request) {

        DataRegionDto data = this.depdDataRegionService.getDataRegionById(id);

        if (data == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

}
