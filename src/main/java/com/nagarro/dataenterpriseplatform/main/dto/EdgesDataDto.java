package com.nagarro.dataenterpriseplatform.main.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EdgesDataDto implements Serializable {

	private String source;

	private String sourceHandle;

	private String target;

	private String targetHandle;

	private String type;

	private Map<String, String> data;

}
