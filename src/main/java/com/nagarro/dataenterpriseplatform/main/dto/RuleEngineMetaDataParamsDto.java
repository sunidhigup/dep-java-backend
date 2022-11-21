package com.nagarro.dataenterpriseplatform.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleEngineMetaDataParamsDto {

    private String id;
    private String client_name;

    private String table_name;

    // @Type(type = "jsonb")
    // @Lob
    private RuleEngineMetaParamsDto paramsMeta;

}
