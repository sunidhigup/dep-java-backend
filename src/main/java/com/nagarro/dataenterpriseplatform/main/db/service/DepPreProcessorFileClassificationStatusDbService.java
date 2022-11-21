package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepPreProcessorFileClassificationStatusRepo;

@Service
@Transactional
public class DepPreProcessorFileClassificationStatusDbService {

    @Autowired
    private DepPreProcessorFileClassificationStatusRepo repo;

    public DepPreProcessorFileClassificationStatusEntity getStatus(
            DepPreProcessorFileClassificationStatusEntity status) {
        return repo.findById(status.getId()).get();
    }

    public String getPreProcessorStatus(String id) {
        final DepPreProcessorFileClassificationStatusEntity preprocesStatus = repo.findById(id).get();
        String status = null;
        if (id != null) {
            status = preprocesStatus.getStatus();
            return status;
        } else {
            return "Invalid Record";
        }
    }

    public DepPreProcessorFileClassificationStatusEntity getPreProcessor(String id) {
        final DepPreProcessorFileClassificationStatusEntity preStatus = repo.findById(id).get();
        return preStatus;

    }

    public List<JSONObject> getPreprocessor(String prefix) {
        final List<DepPreProcessorFileClassificationStatusEntity> preStatus = repo.findAll();
        final List<JSONObject> resultList = new ArrayList<JSONObject>();
        final JSONObject json = new JSONObject();
        for (DepPreProcessorFileClassificationStatusEntity entity : preStatus) {
            if (entity.getId().contains(prefix)) {

                json.put("id", entity.getId());
                json.put("Status", entity.getStatus());
                resultList.add(json);
            }
        }
        return resultList;
    }

    public DepPreProcessorFileClassificationStatusEntity getPreprocessorStatus(String client_name, String batch_name, String table_name) {
        return repo.findByClientBatchTable(client_name, batch_name, table_name);
    }

    public List<JSONObject> getPreprocessorById(String prefix) {
        final List<DepPreProcessorFileClassificationStatusEntity> preStatus = repo.findAll();
        final List<JSONObject> resultList = new ArrayList<JSONObject>();
        final JSONObject json = new JSONObject();
        for (DepPreProcessorFileClassificationStatusEntity entity : preStatus) {
            if (entity.getId().contains(prefix)) {

                json.put("id", entity.getId());
                json.put("Status", entity.getStatus());
                resultList.add(json);
            }
        }
        return resultList;
    }
}
