package com.nagarro.dataenterpriseplatform.main.service;

import org.springframework.http.ResponseEntity;

public interface RegionService {

	/**\
	 * 
	 * @return
	 */
	public ResponseEntity<?>  getInfraRegion();
	
	/**
	 * 
	 * @return
	 */
	public ResponseEntity<?>  getDataRegion();

}
