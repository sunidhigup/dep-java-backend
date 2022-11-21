package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nagarro.dataenterpriseplatform.main.dto.MDMAttributeMetadataDto;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_MDM_entity_metadata")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepMDMEntityMetadataEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)", name = "entity_id")
	private String entityId;

	@Column(nullable = false, name = "entity_name")
	@NotNull(message = "Entity cannot be null.")
	@NotBlank(message = "Entity cannot be blank.")
	private String entityName;

	// @Convert(converter = StringArrayAttributeConverter.class)
	@Column(columnDefinition = "jsonb", length = Integer.MAX_VALUE)
	@Type(type = "jsonb")
	private List<MDMAttributeMetadataDto> attribute;
}
