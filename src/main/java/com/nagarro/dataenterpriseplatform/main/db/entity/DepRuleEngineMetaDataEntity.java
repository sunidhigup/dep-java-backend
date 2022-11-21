package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineMetaParamsDto;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dep_rule_engine_metadata")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepRuleEngineMetaDataEntity implements Serializable {

	@Id
	@Column(columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(columnDefinition = "VARCHAR(255)")
	private String client_name;

	@Column(columnDefinition = "VARCHAR(255)")
	private String table_name;

	@Column(columnDefinition = "TEXT")
	private String params;
}
