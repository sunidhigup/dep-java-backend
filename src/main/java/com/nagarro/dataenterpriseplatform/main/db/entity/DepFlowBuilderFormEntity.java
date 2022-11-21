package com.nagarro.dataenterpriseplatform.main.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.GenericGenerator;

import com.nagarro.dataenterpriseplatform.main.db.converter.StringIntegerMapAttributeConverter;
import com.nagarro.dataenterpriseplatform.main.dto.FlowBuilderFormDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "dep_flow_builder_form")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DepFlowBuilderFormEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(unique = true, columnDefinition = "VARCHAR(36)")
    private String fieldId;

    @Column
    private String batch_id;

    @Column
    private String client_id;

    @Column
    private String batch;

    @Column(name = "job_name")
    private String jobName;

    @Column(columnDefinition = "jsonb", length = Integer.MAX_VALUE)
    @Type(type = "jsonb")
    private List<FlowBuilderFormDto> nodes;
}
