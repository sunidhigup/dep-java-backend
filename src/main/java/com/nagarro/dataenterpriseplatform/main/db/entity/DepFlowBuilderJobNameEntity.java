package com.nagarro.dataenterpriseplatform.main.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "dep_flowbuilder_job_name")
public class DepFlowBuilderJobNameEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(unique = true)
    private String job_id;

    @Column(name = "active")
    private boolean is_active = true;

    @Column
    private String batch_id;

    @Column
    private String batch_name;

    @Column
    private String bucket;

    @Column
    private String client_id;

    @Column
    private String client_name;

    @Column(name = "connection_type")
    private String connectionType;

    @Column(name = "connection_name")
    private String connectionName;

    @Column(columnDefinition = "TEXT")
    private String extracts;

    @Column
    private String input_ref_key;

    @Column
    private String status = "pending";


}
