package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepStreamMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepStreamMetadataRepo;

@Service
@Transactional
public class DepStreamMetadataDbService {

	@Autowired
	private DepStreamMetadataRepo repo;

	public boolean addStream(DepStreamMetadataEntity stream) {
		System.out.println(stream);

		DepStreamMetadataEntity newStream = repo.save(stream);
		if (newStream != null)
			return true;
		else
			return false;
	}

	public List<DepStreamMetadataEntity> getAllStreams() {
		return repo.findAll();
	}

	public List<DepStreamMetadataEntity> getStreamByClientName(String client_name) {
		final List<DepStreamMetadataEntity> list = repo.findByClientName(client_name);
		return list;

	}

	public DepStreamMetadataEntity getStreamByStreamName(String stream_name) {
		final DepStreamMetadataEntity stream = repo.findByStream_name(stream_name);
		return stream;

	}

	public DepStreamMetadataEntity getStreamByStreamNameAndClientName(String clientName, String streamName) {
		final DepStreamMetadataEntity stream = repo.findByClient_nameAndStream_name(clientName, streamName);
		return stream;
	}
}
