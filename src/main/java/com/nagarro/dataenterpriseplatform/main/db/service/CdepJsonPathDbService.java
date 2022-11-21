package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;


import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.CdepJsonPathEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.CdepJsonPathRepo;

@Service
@Transactional
public class CdepJsonPathDbService {

	@Autowired
	private CdepJsonPathRepo cdepJsonPathRepo;
	
	
	public void addS3Path(CdepJsonPathEntity data) {
		final List<CdepJsonPathEntity> result =  cdepJsonPathRepo.findByClientIdTableAndBatch(data.getClient_id(), data.getTable_name(), data.getBatch_name());
		if (result.size() > 0) {
			cdepJsonPathRepo.delete(result.get(0));
		}
		cdepJsonPathRepo.save(data);
	}
	
	public CdepJsonPathEntity getS3Path(String client_id, String tablename, String batchname) {
		final List<CdepJsonPathEntity> result =  cdepJsonPathRepo.findByClientIdTableAndBatch(client_id,tablename, batchname);
		if (result.size() > 0) {
			return result.get(0);
		}

		return null;
	}

}
