package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.nagarro.dataenterpriseplatform.main.dto.NodesDataDto;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_mdm_flow_builder_nodes")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepMdmFlowBuilderNodesEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)", name = "field_name")
	private String fieldId;

	@Column(name = "entity_name")
	private String entityName;

	// @Convert(converter = StringArrayAttributeConverter.class)
	@Column(columnDefinition = "jsonb", length = Integer.MAX_VALUE)
	@Type(type = "jsonb")
	private List<NodesDataDto> nodes;
}
