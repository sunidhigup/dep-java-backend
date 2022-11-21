package com.nagarro.dataenterpriseplatform.main.db.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.transaction.Transactional;

import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.dto.TableJsonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepTableRulesRepo;

@Service
@Transactional
public class DepTableRulesDbService {

//    @Value("${aws.bucketName}")
//    private String bucketname;

@Autowired
private DepClientDbService depClientDbService;

    @Autowired
    private DepTableRulesRepo depTableRulesRepo;

    @Autowired
    private AmazonS3 amazonS3;

    public void addTableRule(DepTableRulesEntity rule) {

        try {
            final List<DepTableRulesEntity> fetchedRule = depTableRulesRepo
                    .findByClientIdTableAndBatch(rule.getClient_id(), rule.getTablename(), rule.getBatchname());
            if (fetchedRule.size() > 0) {
                DepTableRulesEntity fetchedEntity = fetchedRule.get(0);
                fetchedEntity.setFields(rule.getFields());
                fetchedEntity.setPath(rule.getPath());
                fetchedEntity.setGenerated(false);
                depTableRulesRepo.save(fetchedEntity);
                return;
//                depTableRulesRepo.deleteById(fetchedRule.get(0).getId());
            }
            depTableRulesRepo.save(rule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<DepTableRulesEntity> fetchTableRule(String client_id, String batchname) {
        final List<DepTableRulesEntity> fetchedRule = depTableRulesRepo.findByClientIdContainsAndBatchname(client_id,
                batchname);
        if (fetchedRule.size() == 0) {
            return null;
        }
        return fetchedRule;
    }

    public DepTableRulesEntity fetchTableRuleById(String table_id) {
        return depTableRulesRepo.findById(table_id).orElse(null);

    }

    public boolean DeleteTableRule(String clientId, String batchname, String tablename) {
        final List<DepTableRulesEntity> result = depTableRulesRepo.findByClientIdTableAndBatch(clientId, tablename,
                batchname);
        if (result.size() == 1) {
            depTableRulesRepo.delete(result.get(0));
            DepClientEntity clientData = depClientDbService.getClientById(clientId);
            final String PREFIX = clientId + "/" + batchname + "/" + tablename;
            final ListObjectsRequest request = new ListObjectsRequest();
            request.setBucketName(clientData.getDataRegionEntity().getBucket_name());
            request.setPrefix(PREFIX);
            final ObjectListing response = amazonS3.listObjects(request);

            for (S3ObjectSummary res : response.getObjectSummaries()) {
                DeleteObjectRequest req = new DeleteObjectRequest(clientData.getDataRegionEntity().getBucket_name(), res.getKey());
                amazonS3.deleteObject(req);
            }

        }
        return true;

    }

    public DepTableRulesEntity FetchTableRule(String client_id, String batch_name, String tableName) {
        final List<DepTableRulesEntity> result = depTableRulesRepo.findByClientIdTableAndBatch(client_id, tableName,
                batch_name);
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

    public List<DepTableRulesEntity> fetchTableRuleByCBT(String client_id, String batchname, String tablename) {
        final List<DepTableRulesEntity> fetchedRule = depTableRulesRepo.findByClientIdTableAndBatch(client_id,
                tablename, batchname);
        return fetchedRule;
    }


    public void getS3FileNStore(String client_id, String batchname, String tablename, String path) {
        try {
            DepClientEntity clientData = depClientDbService.getClientById(client_id);
            S3Object o = amazonS3.getObject(clientData.getDataRegionEntity().getBucket_name(), path);

            S3ObjectInputStream s3is = o.getObjectContent();

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(s3is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            TableJsonDto json = gson.fromJson(sb.toString(), TableJsonDto.class);
            DepTableRulesEntity rulesData = new DepTableRulesEntity();

            rulesData.setClient_id(client_id);
            rulesData.setBatchname(batchname);
            rulesData.setTablename(tablename);
            rulesData.setPath("s3://" + clientData.getDataRegionEntity().getBucket_name() + "/" + path);
            rulesData.setFields(json.getFields());
            rulesData.setGenerated(false);

            this.addTableRule(rulesData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
