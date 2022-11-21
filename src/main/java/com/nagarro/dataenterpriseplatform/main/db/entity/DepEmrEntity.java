package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dep_emr")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepEmrEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(unique = true)
	private String cluster_id;

	@Column(name = "available_cores")
	private String Available_cores;
	
	@Column(columnDefinition = "text")
	private String job_names;

	@Column(name = "job_queue",columnDefinition = "text")
	private String Job_Queue;

	@Column(name = "queue_name",columnDefinition = "text")
	private String Queue_name;

	@Column
	private String status;

	@Column(columnDefinition = "text")
	private String Step_list;
	
	@Column(name = "total_cores")
	private int Total_cores;

}
