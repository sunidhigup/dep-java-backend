package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadNodesDto {

	private List<ReadFormDto> readForms;
}
