package com.nagarro.dataenterpriseplatform.main.dto;


import lombok.Data;
import lombok.NonNull;

@Data
public class AddWareHouseDto {

	@NonNull
	String name;
	
	
	String warehouse_size;
	int max_xluster_count;
	int min_cluster_count;
	String scaling_policy;
	int auto_suspend;
	boolean auto_resume;
	String comments;
	boolean  enable_query_acceleration;
	int query_acceleration_max_scale_factor;
	String resource_monitor;
	boolean initially_suspended;
	
}
