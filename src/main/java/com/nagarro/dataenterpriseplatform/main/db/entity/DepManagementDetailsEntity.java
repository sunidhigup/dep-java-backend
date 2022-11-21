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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dep_management_details")
public class DepManagementDetailsEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true, columnDefinition = "VARCHAR(36)")
	private String id;

	@Column
	private String username;

	@Column
	private String password;

	@Column
	private String email;

	@Column(name = "domain_type")
	private String domainType;

	@Column
	private String status;

	@Column
	private String role;

	@Column
	private String admin_id;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
	@JsonManagedReference
	@JsonIgnore
	private List<DepClientEntity> client = new ArrayList<DepClientEntity>();
}
