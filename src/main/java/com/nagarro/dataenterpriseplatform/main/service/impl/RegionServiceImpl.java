package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsEc2Client;
import com.nagarro.dataenterpriseplatform.main.constants.ApplicationConstants;
import com.nagarro.dataenterpriseplatform.main.service.RegionService;

@Service
public class RegionServiceImpl implements RegionService {
	
	@Autowired
	private AwsEc2Client amazonEC2;

	@Override
	public ResponseEntity<?> getInfraRegion() {
		final HashMap<String, Object> apiResponse = new HashMap<>();
		com.amazonaws.regions.Region region = Regions.getCurrentRegion();
		if(region == null) {
			region =  com.amazonaws.regions.Region.getRegion(Regions.US_EAST_1);
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, region.getName());
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getDataRegion() {
		final List<HashMap<String, Object>> arrayListObject = new ArrayList<>();
		final HashMap<String, Object> apiResponse = new HashMap<>();
		final DescribeRegionsResult regions_response  = amazonEC2.getAmazonSec2Client().describeRegions();
		for(Region region : regions_response.getRegions()) {
			final HashMap<String, Object> response = new HashMap<>();
			response.put(ApplicationConstants.LABEL, region.getRegionName().toLowerCase());
			response.put(ApplicationConstants.VALUE, region.getRegionName());
			arrayListObject.add(response);
		}
		apiResponse.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);
		apiResponse.put(ApplicationConstants.DATA, arrayListObject);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}
