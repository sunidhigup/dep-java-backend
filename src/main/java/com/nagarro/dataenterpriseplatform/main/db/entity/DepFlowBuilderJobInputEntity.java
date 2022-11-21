package com.nagarro.dataenterpriseplatform.main.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "dep_flowbuilder_job_input")
public class DepFlowBuilderJobInputEntity {

    @Id
    @Column(name = "unique_id")
    private String uniqueId;

    @Column
    private String batch_id;

    @Column
    private String client_id;

    @Column
    private String job;

    @Column
    private String bucket;

    @Column
    private String extensions;

    @Column
    private String extract;

    @Column
    private String prefix;
}
