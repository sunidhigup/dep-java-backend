package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepDataRegionEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepClientRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepDataRegionRepo;
import com.nagarro.dataenterpriseplatform.main.dto.DataRegionDto;

@Service
public class DepDataRegionService {

	@Autowired
	DepDataRegionRepo depDataRegionRepo;

	@Autowired
	private DepClientRepo depClientRepo;

	public List<DepDataRegionEntity> getAllDataRegions() {
		return this.depDataRegionRepo.findAll();
	}

	public DataRegionDto getDataRegionById(String id) {

		DepDataRegionEntity data = this.depDataRegionRepo.findByDataRegionId(id);
		DataRegionDto dataRegionDto = new DataRegionDto();
		dataRegionDto.setData_region_name(data.getData_region_name());
		dataRegionDto.setData_region_code(data.getData_region_code());
		dataRegionDto.setBucket_name(data.getBucket_name());
		dataRegionDto.setData_region_arn(data.getData_region_arn());
		return dataRegionDto;
	}

	public DepDataRegionEntity getDataRegionByRegionCode(String code) {

		return this.depDataRegionRepo.findByCode(code);
	}

	public void insertDataRegion(DepDataRegionEntity dataRegion) {
		this.depDataRegionRepo.save(dataRegion);
	}

	public List<DepClientEntity> getClientsByDataRegionById(String id) {
		return this.depClientRepo.findByDataRegionId(id);
	}

	public List<DepClientEntity> getClientsDataRegionById(String id) {
		return this.depClientRepo.findByDataRegionId(id);

	}

}
