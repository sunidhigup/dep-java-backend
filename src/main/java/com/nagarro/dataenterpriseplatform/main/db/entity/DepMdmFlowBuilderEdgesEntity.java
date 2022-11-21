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

import com.nagarro.dataenterpriseplatform.main.dto.EdgesDataDto;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dep_mdm_flow_builder_edges")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepMdmFlowBuilderEdgesEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, name = "field_id")
	private String fieldId;

	@Column(name = "entity_name")
	private String entityName;

	@Column(columnDefinition = "jsonb", length = Integer.MAX_VALUE)
	@Type(type = "jsonb")
	private List<EdgesDataDto> edges;

}
