package com.nagarro.dataenterpriseplatform.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.service.RegionService;

@RestController
@RequestMapping("/api/region")
@CrossOrigin(origins = "*")
public class RegionController {

	@Autowired
	private RegionService regionService;
	
	
	@GetMapping("/fetch-infra-region")
	public ResponseEntity<?> getBatch(HttpServletRequest request, HttpServletResponse response) {
		return regionService.getInfraRegion();
	}

	@GetMapping("/fetch-data-region")
	public ResponseEntity<?> getDataRegion(HttpServletRequest request, HttpServletResponse response) {
		return regionService.getDataRegion();
		
	}
	
}
