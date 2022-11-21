package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepPreProcessorFileClassificationRepo;

@Service
@Transactional
public class DepPreProcessorFileClassificationDbService {

	@Autowired
	private DepPreProcessorFileClassificationRepo repo;

	public boolean chkPreprocessorReqd(String id, Boolean preprocessorFlag) {
		final DepPreProcessorFileClassificationEntity preprocessExist = repo.findById(id).get();
		return !preprocessorFlag && preprocessorFlag.toString().equals(preprocessExist.getSkip_PreProcess());
	}

	public boolean createPreprocessor(DepPreProcessorFileClassificationEntity preprocess) {
		final DepPreProcessorFileClassificationEntity preprocessExist = repo.findById(preprocess.getId()).orElse(null);
		if (preprocessExist == null) {
			repo.save(preprocess);
			return true;
		} else {
			return false;
		}
	}

	public List<DepPreProcessorFileClassificationEntity> getAllPreprocessor() {
		return repo.findAll();
	}

	public List<DepPreProcessorFileClassificationEntity> getPreProcessorClientBatch(String client_name,
			String batch_name) {
		final List<DepPreProcessorFileClassificationEntity> fetchedPreProcess = repo
				.findByClient_nameAndBatch_name(client_name, batch_name);
		if (fetchedPreProcess.size() == 0) {
			return null;
		}
		return fetchedPreProcess;
	}

	public boolean chkPreprocessorExist(String id) {
		final DepPreProcessorFileClassificationEntity preprcoess = repo.findById(id).get();
		if (preprcoess != null) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean DisableEnableJob(DepPreProcessorFileClassificationEntity data) {
		final DepPreProcessorFileClassificationEntity preprcoess = repo.findById(data.getId()).get();
		if (preprcoess != null) {
			preprcoess.setDisableJob(data.getDisableJob());
			repo.save(preprcoess);
			return true;
		}
		return false;
	}

	public void updatePreprocess(DepPreProcessorFileClassificationEntity preprocess) {
		final DepPreProcessorFileClassificationEntity deletePreprocess = repo.findById(preprocess.getId()).get();

		repo.delete(deletePreprocess);
		repo.save(preprocess);
	}
}
