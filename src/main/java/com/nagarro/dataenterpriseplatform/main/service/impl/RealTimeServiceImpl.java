package com.nagarro.dataenterpriseplatform.main.service.impl;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepStreamMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepStreamMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.dto.EmrClusterDetails;
import com.nagarro.dataenterpriseplatform.main.dto.RealTimeResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.RealTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealTimeServiceImpl implements RealTimeService {

    @Autowired
    AmazonElasticMapReduce emr;

    @Autowired
    private DepStreamMetadataDbService depStreamMetadataDbService;

    @Override
    public List<RealTimeResponseDto> getAllStreamsAndStatus(String clientName) {
        final List<RealTimeResponseDto> listDtoObject = new ArrayList<>();
        final List<DepStreamMetadataEntity> streamResponse = depStreamMetadataDbService
                .getStreamByClientName(clientName);

        if (streamResponse == null) return null;

        for (DepStreamMetadataEntity entity : streamResponse) {
            final EmrClusterDetails statusRes = fetchEmrClusterDetails(entity.getClient_name() + "_" + entity.getTable_name(), null);
            final RealTimeResponseDto dto = new RealTimeResponseDto();
            dto.setId(entity.getId());
            dto.setStream_name(entity.getStream_name());
            dto.setClient_name(entity.getClient_name());
            dto.setStream_id(entity.getStream_id());
            dto.setTable_name(entity.getTable_name());
            dto.setRuleEngine(entity.isRuleEngine());
            dto.setStatus(statusRes != null ? statusRes.getStatus() : "Unknown");
            dto.setClusterId(statusRes != null ? statusRes.getClusterId() : null);
            dto.setClusterName(statusRes != null ? statusRes.getClusterName() : null);
            dto.setBucket(entity.getBucket());
            dto.setRegion(entity.getRegion());
            dto.setFlowBuilder(entity.isFlowBuilder());
            dto.setJobFilePrefix(entity.getJobFilePrefix());
            dto.setProcessing(entity.getProcessing());
            dto.setStorage(entity.isStorage());
            listDtoObject.add(dto);
        }
        return listDtoObject;
    }

    public EmrClusterDetails fetchEmrClusterDetails(String clusterName, String clusterId) {
        EmrClusterDetails cluster = new EmrClusterDetails();

        if (clusterName == null && clusterId == null) {

            cluster.setClusterId("unknown");
            cluster.setClusterName("unknown");
            cluster.setStatus("unknown");
            return cluster;

        }

        List<ClusterSummary> result = emr.listClusters().getClusters();

        if (clusterName != null && clusterName.length() > 1) {

            result = result.stream().filter((element) -> element.getName().equals(clusterName))
                    .collect(Collectors.toList());

        }

        List<ClusterSummary> runningCluster = result.stream()
                .filter((e) -> e.getStatus().getState().equals("STARTING") || e.getStatus().getState().equals("RUNNING")
                        || e.getStatus().getState().equals("WAITING") || e.getStatus().getState().equals("TERMINATING"))
                .collect(Collectors.toList());

        if (!runningCluster.isEmpty()) {
            result = runningCluster;
        }
        if (clusterId != null && !clusterId.equals("unknown")) {

            result = result.stream().filter((element) -> (element.getId().equals(clusterId))

            ).collect(Collectors.toList());
        }

        result.stream().forEach((e) -> {

            cluster.setClusterId(e.getId());
            cluster.setClusterName(e.getName());
            cluster.setStatus(e.getStatus().getState());

        });

        if (result.isEmpty()) {

            cluster.setClusterId("unknown");
            cluster.setClusterName("unknown");
            cluster.setStatus("unknown");
        }

        return cluster;
    }

    @Override
    public RealTimeResponseDto clusterStatus(String clientName, String streamName, String clusterName, String clusterId) {

        final DepStreamMetadataEntity entity = depStreamMetadataDbService.getStreamByStreamNameAndClientName(clientName, streamName);

        final EmrClusterDetails statusRes = fetchEmrClusterDetails(entity.getClient_name() + "_" + entity.getTable_name(), null);
        final RealTimeResponseDto dto = new RealTimeResponseDto();
        dto.setId(entity.getId());
        dto.setStream_name(entity.getStream_name());
        dto.setClient_name(entity.getClient_name());
        dto.setStream_id(entity.getStream_id());
        dto.setTable_name(entity.getTable_name());
        dto.setRuleEngine(entity.isRuleEngine());
        dto.setStatus(statusRes != null ? statusRes.getStatus() : "Unknown");
        dto.setClusterId(statusRes != null ? statusRes.getClusterId() : null);
        dto.setClusterName(statusRes != null ? statusRes.getClusterName() : null);
        dto.setBucket(entity.getBucket());
        dto.setRegion(entity.getRegion());
        dto.setFlowBuilder(entity.isFlowBuilder());
        dto.setJobFilePrefix(entity.getJobFilePrefix());
        dto.setProcessing(entity.getProcessing());
        dto.setStorage(entity.isStorage());
        return dto;
    }
}
