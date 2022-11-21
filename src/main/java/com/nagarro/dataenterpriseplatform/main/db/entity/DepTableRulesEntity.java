package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.nagarro.dataenterpriseplatform.main.dto.TableJsonFieldsDto;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_table_rules")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepTableRulesEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(255)")
	private String id;

	@Column(columnDefinition = "text")
	private String tablename;

	@Column(columnDefinition = "text")
	private String batchname;

	@Column(columnDefinition = "text")
	private String path;

	@Column(columnDefinition = "text")
	private String client_id;

	@Column(columnDefinition = "text")
	private Boolean generated;

	@Column(columnDefinition = "jsonb", length = Integer.MAX_VALUE)
	@Type(type = "jsonb")
	@Lob
	private List<TableJsonFieldsDto> fields;
}
