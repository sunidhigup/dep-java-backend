package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeResponseDto {

    private String stream_name;

    private String id;

    private String stream_id;

    private String client_name;

    private String table_name;

    private boolean ruleEngine;

    private boolean storage;

    private boolean flowBuilder;

    private String bucket;

    private String region;

    private String jobFilePrefix;

    private String processing;

    private String clusterId;

    private String clusterName;

    private String status;


}
