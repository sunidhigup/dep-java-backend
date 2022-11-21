package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderJobNameDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private DepClientDbService depClientDbService;

	@Autowired
	private DepFlowBuilderMetadataDbService depFlowBuilderMetadataDbService;

	@Autowired
	private DepFlowBuilderJobNameDbService depFlowBuilderJobNameDbService;

	@Override
	public List<LinkedHashMap<String, Object>> getInfo(String userId) {
		final List<LinkedHashMap<String, Object>> finalResp = new ArrayList<>();
		final LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		final List<DepClientEntity> clientInfo = depClientDbService.findClientByUserId(userId);
		try {
			if (clientInfo != null) {
				final List<Map<String, Object>> clientResp = new ArrayList<>();
				for (DepClientEntity client : clientInfo) {
					final Map<String, Object> respClinet = new HashMap<>();
					respClinet.put("client_id", client.getClient_id());
					respClinet.put("client_name", client.getClient_name());
					respClinet.put("year_created", client.getYear_created());
					respClinet.put("status", client.getStatus());
					respClinet.put("infra_region", client.getInfra_region());
					// System.out.println("res"+clientResp);
					final List<DepFlowBuilderMetadataEntity> batchInfo = depFlowBuilderMetadataDbService
							.getAllBatch(client.getClient_id());
					if (batchInfo != null) {
						final List<Map<String, Object>> batchResp = new ArrayList<>();
						for (DepFlowBuilderMetadataEntity batch : batchInfo) {
							final Map<String, Object> respBatch = new HashMap<>();
							respBatch.put("client_id", client.getClient_id());
							respBatch.put("client_name", client.getClient_name());
							respBatch.put("batch_id", batch.getBatch_id());
							respBatch.put("batch_name", batch.getBatch_name());
							respBatch.put("status", batch.getStatus());
							batchResp.add(respBatch);
							final List<DepFlowBuilderJobNameEntity> jobInfo = depFlowBuilderJobNameDbService
									.getAllJobs(client.getClient_id(), batch.getBatch_id());
							if (jobInfo != null) {
								final List<Map<String, Object>> jobResp = new ArrayList<>();
								for (DepFlowBuilderJobNameEntity job : jobInfo) {
									final Map<String, Object> respJob = new HashMap<>();
									respJob.put("client_id", job.getClient_id());
									respJob.put("client_name", job.getClient_name());
									respJob.put("batch_id", job.getBatch_id());
									respJob.put("batch_name", job.getBatch_name());
									respJob.put("job_id", job.getJob_id());
									respJob.put("input_ref_key", job.getInput_ref_key());
									respJob.put("status", job.getStatus());
									jobResp.add(respJob);
								}
								respClinet.put("job_info", jobResp);
							}

						}
						respClinet.put("batch_info", batchResp);

					}
					clientResp.add(respClinet);
				}

				// System.out.println(clientResp);
				response.put("client_info", clientResp);
				finalResp.add(response);
			}
		} catch (Exception e) {
			System.out.println("res" + e);
		}
		return finalResp;
	}

}
