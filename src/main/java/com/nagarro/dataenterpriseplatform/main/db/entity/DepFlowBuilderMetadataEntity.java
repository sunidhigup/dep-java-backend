package com.nagarro.dataenterpriseplatform.main.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "dep_flowbuilder_metadata")
public class DepFlowBuilderMetadataEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(unique = true, columnDefinition = "VARCHAR(36)")
    private String batch_id;

    @Column
    private String client_id;

    @Column
    private String batch_name;

    @Column
    @NotNull(message = "Batch type cannot be null.")
    @NotBlank(message = "Batch type cannot be blank.")
    private String batch_type;

    @Column(name = "active")
    private String is_active = "true";

    @Column
    @NotNull(message = "Job cannot be null.")
    @NotBlank(message = "Job cannot be blank.")
    private String job;

    @Column(columnDefinition = "TEXT")
    private String log_group;

    @Column
    private String status = "pending";
    
    private String client_name;


}
