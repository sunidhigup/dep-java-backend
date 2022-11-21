package com.nagarro.dataenterpriseplatform.main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleFormDto {

    private String columnType;
    private String decimaldefaultvalue;
    private String numericcharacters;
    private String setscaleroundingmode;
}
