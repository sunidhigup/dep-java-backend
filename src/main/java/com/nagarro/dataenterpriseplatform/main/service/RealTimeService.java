package com.nagarro.dataenterpriseplatform.main.service;

import com.nagarro.dataenterpriseplatform.main.dto.RealTimeResponseDto;

import java.util.List;

public interface RealTimeService {

    /**
     *
     * @param client_name
     * @return
     */
    List<RealTimeResponseDto> getAllStreamsAndStatus(String clientName);

    /**
     *
     * @param clientName
     * @param streamName
     * @param clusterName
     * @param clusterId
     * @return
     */
    RealTimeResponseDto clusterStatus(String clientName, String streamName, String clusterName, String clusterId);
}
