package com.nagarro.dataenterpriseplatform.main.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodesDataDto implements Serializable {

	private String id;

	private String type;

	private Map<String, Double> position;

	private Map<String, Double> positionAbsolute;

	private Map<String, String> data;

	private Boolean dragging;

	private Boolean selected;

	private Integer width;

	private Integer height;
}
