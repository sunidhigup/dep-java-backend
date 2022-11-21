package com.nagarro.dataenterpriseplatform.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRegionDto {

    private String data_region_code;
    private String data_region_name;
    private String bucket_name;
    private String data_region_arn;
}
