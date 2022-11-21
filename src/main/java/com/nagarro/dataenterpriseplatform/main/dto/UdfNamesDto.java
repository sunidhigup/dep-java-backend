package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UdfNamesDto {
	  private List<UdfListDto> udf_names;
}
