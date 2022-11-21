package com.nagarro.dataenterpriseplatform.main.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UdfListDto {
    private String udf_name;
    private List<String> data_type;
}
