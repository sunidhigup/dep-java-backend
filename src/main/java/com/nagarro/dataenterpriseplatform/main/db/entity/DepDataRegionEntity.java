package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dep_data_region")
public class DepDataRegionEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(unique = true)
	private String data_region_code;

	@Column
	private String data_region_name;

	@Column
	private String bucket_name;

	@Column
	private String data_region_arn;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "dataRegionEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
	@JsonManagedReference
	@JsonIgnore
	private List<DepClientEntity> client = new ArrayList<DepClientEntity>();

}
