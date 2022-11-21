package com.nagarro.dataenterpriseplatform.main.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import com.nagarro.dataenterpriseplatform.main.AWS.config.AwsStepFunctionConfiguration;
import com.nagarro.dataenterpriseplatform.main.AWS.multitenant.AWSS3Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nagarro.dataenterpriseplatform.main.db.entity.CdepJsonPathEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineMetaDataEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.CdepJsonPathDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepRuleEngineJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepRuleEngineMetaDataDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepTableRulesDbService;
import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineMetaDataParamsDto;
import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineMetaParamsDto;
import com.nagarro.dataenterpriseplatform.main.dto.RuleEngineResponseDto;
import com.nagarro.dataenterpriseplatform.main.dto.TableInputDto;
import com.nagarro.dataenterpriseplatform.main.dto.TableJsonDto;
import com.nagarro.dataenterpriseplatform.main.dto.UploadJsonRequest;
import com.nagarro.dataenterpriseplatform.main.service.TableRuleService;
import com.nagarro.dataenterpriseplatform.main.utils.Utility;

/*
 * Controller class for table level operations
 * */

@RestController
@RequestMapping("/api/table-rule")
@CrossOrigin(origins = "*")
public class TableRuleController {

    // @Value("${aws.bucketName}")
    // private String awsBucketName;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${jar_folder_path}")
    private String jar_folder_path;

    @Value("${aws.accessKey}")
    private String dynamodbAccessKey;

    @Value("${aws.secretKey}")
    private String dynamodbSecretKey;

    @Value("${aws.sessionToken}")
    private String dynamodbSessionToken;

    @Autowired
    private DepClientDbService depClientDbService;

    // @Autowired
    // private AmazonS3 amazonS3;

    @Autowired
    private DepTableRulesDbService depTableRulesDbService;

    @Autowired
    private CdepJsonPathDbService cdepJsonPathDbService;

    @Autowired
    private DepRuleEngineJobStatusDbService depRuleEngineJobStatusDbService;

    @Autowired
    private DepRuleEngineMetaDataDbService depRuleEngineMetadataDbService;

    @Autowired
    private TableRuleService tableRuleService;

    /*
     * API for fetching table layout json from s3
     */

    @PostMapping("/get-s3-table-rules")
    public ResponseEntity<?> getTableRules(@RequestBody TableInputDto tab, HttpServletRequest request,
            HttpServletResponse response) {

        try {
            DepClientEntity clientData = depClientDbService.getClientById(tab.getClient_id());

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = clientData.getDataRegionEntity().getData_region_code();

            S3Object o = AWSS3Configuration.amazonS3Config()
                    .getObject(clientData.getDataRegionEntity().getBucket_name(), tab.getPath());
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

            return ResponseEntity.status(HttpStatus.OK).body(json);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /*
     * API to fetch csv file of table schema from s3
     */

    @PostMapping("/get-csv-data")
    public ResponseEntity<?> getCSVData(@RequestBody TableInputDto tab, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            DepClientEntity clientData = depClientDbService.getClientById(tab.getClient_id());
            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = clientData.getDataRegionEntity().getData_region_code();

            S3Object o = AWSS3Configuration.amazonS3Config()
                    .getObject(clientData.getDataRegionEntity().getBucket_name(), tab.getPath());
            S3ObjectInputStream s3is = o.getObjectContent();
            StringBuilder sb = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(s3is, "UTF-8"));
            String trim = reader.readLine().trim();
            String[] header = trim.split(",");

            String line;
            List<String> headers = new ArrayList<>();

            for (String s : header) {
                if (!Objects.equals(s, "")) {
                    headers.add(s);
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(headers);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // get field name from json

    @PostMapping("/get-json-data")
    public ResponseEntity<?> getJsonData(@RequestBody TableInputDto tab, HttpServletRequest request,
            HttpServletResponse response) {

        try {
            DepClientEntity clientData = depClientDbService.getClientById(tab.getClient_id());

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = clientData.getDataRegionEntity().getData_region_code();

            S3Object o = AWSS3Configuration.amazonS3Config()
                    .getObject(clientData.getDataRegionEntity().getBucket_name(), tab.getPath());
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

            List<String> fieldname = json.getFields().stream().map((e) -> e.getFieldname())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(fieldname);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /*
     * API to create json file from csv file and store it in s3
     */

    @PostMapping(value = "/create-csv-to-json/{client_id}/{batchname}/{tablename}")
    public ResponseEntity<?> csvToJson(@RequestBody String form, @PathVariable("client_id") String client_id,
            @PathVariable("batchname") String batchname, @PathVariable("tablename") String tablename,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            final DepClientEntity client = this.depClientDbService.getClientById(client_id);

            if (client == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            final String bucketName = client.getDataRegionEntity().getBucket_name();
            final String fileName = client.getClient_name() + "/" + batchname + "/" + tablename
                    + "/Rule_engine/Table-Json/" + tablename + ".json";

            AWSS3Configuration.amazonS3Config().putObject(bucketName, fileName, form);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
        }
    }

    /*
     * API to create table rule
     */

    @PostMapping("/add-table-rule")
    public ResponseEntity<?> createTableRule(@RequestBody DepTableRulesEntity rule, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            depTableRulesDbService.addTableRule(rule);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to fetch table rule By Client , batch, table
     */

    @GetMapping("/get-table-rule/{client_id}/{batchname}/{tablename}")
    public ResponseEntity<List<DepTableRulesEntity>> getTableRuleByCBT(@PathVariable("client_id") String client_id,
            @PathVariable("batchname") String batchname, @PathVariable("tablename") String tablename,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            final List<DepTableRulesEntity> list = depTableRulesDbService.fetchTableRuleByCBT(client_id, batchname,
                    tablename);
            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /*
     * API to fetch table rule
     */

    @GetMapping("/get-table-rule/{client_id}/{batchname}")
    public ResponseEntity<List<DepTableRulesEntity>> getTableRule(@PathVariable("client_id") String client_id,
            @PathVariable("batchname") String batchname, HttpServletRequest request, HttpServletResponse response) {
        try {
            final List<DepTableRulesEntity> list = depTableRulesDbService.fetchTableRule(client_id, batchname);
            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to fetch table rule by id
     */
    @GetMapping("/get-rule-by-id/{table_id}")
    public ResponseEntity<DepTableRulesEntity> getTableRule(@PathVariable("table_id") String table_id,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            final DepTableRulesEntity rule = depTableRulesDbService.fetchTableRuleById(table_id);

            if (rule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(rule);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getRules/{client_id}/{batch_name}/{tableName}")
    public ResponseEntity<DepTableRulesEntity> getTableRule(@PathVariable("client_id") String client_id,
            @PathVariable("batch_name") String batch_name, @PathVariable("tableName") String tableName,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            final DepTableRulesEntity rule = depTableRulesDbService.FetchTableRule(client_id, batch_name, tableName);

            if (rule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(rule);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-s3-path")
    public ResponseEntity<?> createPath(@RequestBody CdepJsonPathEntity data, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            cdepJsonPathDbService.addS3Path(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to create table rule json
     */

    @PostMapping(value = "/table-rule-json/{client_id}/{batchname}/{tablename}/{idt}")
    public ResponseEntity<?> convertData(@RequestBody String form, @PathVariable("client_id") String client_id,
            @PathVariable("batchname") String batchname, @PathVariable("tablename") String tablename,
            @PathVariable("idt") String idt, HttpServletRequest request, HttpServletResponse response) {
        try {

            DepClientEntity client = this.depClientDbService.getClientById(client_id);

            if (client == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            String bucketName;
            String fileName;

            bucketName = client.getDataRegionEntity().getBucket_name();
            fileName = client.getClient_name() + "/" + batchname + "/" + tablename + "/Rule_engine/Rule/" + tablename
                    + ".json";

            // fileName = client.getClient_name() + "/" + batchname + "/" + tablename +
            // "/Rule_engine/Rule/" + idt + "/" + tablename + ".json";

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = client.getDataRegionEntity().getData_region_code();

            PutObjectResult result = AWSS3Configuration.amazonS3Config().putObject(bucketName, fileName, form);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/rule-engine-metadata/{client_id}")
    public ResponseEntity<?> createMetadata(@RequestBody RuleEngineMetaDataParamsDto ruleData,
            @PathVariable("client_id") String client_id,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            DepClientEntity clientData = depClientDbService.getClientById(client_id);

            ObjectMapper mapperObj = new ObjectMapper();
            RuleEngineMetaParamsDto params = ruleData.getParamsMeta();
            params.setBucket_name(clientData.getDataRegionEntity().getBucket_name());
            params.setRegion_name(awsRegion);
            params.setJar_folder_path(jar_folder_path);
            String jsonResp = mapperObj.writeValueAsString(params);
            DepRuleEngineMetaDataEntity md = new DepRuleEngineMetaDataEntity();
            md.setParams(jsonResp);
            md.setId(ruleData.getId());
            md.setClient_name(ruleData.getClient_name());
            md.setTable_name(ruleData.getTable_name());
            depRuleEngineMetadataDbService.addTableRuleMetadata(md);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API for fetching step function status
     */

    @PostMapping("/get-status")
    public ResponseEntity<?> createBatch(@RequestBody DepRuleEngineJobStatusEntity id, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            final DepRuleEngineJobStatusEntity list = depRuleEngineJobStatusDbService.getStatus(id);

            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @PostMapping("/get-status-table")
    public ResponseEntity<?> getStausTable(@RequestParam String id, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            final DepRuleEngineJobStatusEntity list = depRuleEngineJobStatusDbService.getStatusTable(id);

            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @GetMapping("/get-list-s3/{client_id}")
    public ResponseEntity<?> getS3List(@PathParam(value = "prefix") String prefix,
            @PathVariable("client_id") String client_id) {
        DepClientEntity clientData = depClientDbService.getClientById(client_id);

        List<String> jsonFile = new ArrayList<>();

        AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
        AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
        AWSS3Configuration.sessionToken = dynamodbSessionToken;
        AWSS3Configuration.awsRegion = clientData.getDataRegionEntity().getData_region_code();

        ListObjectsV2Result result = AWSS3Configuration.amazonS3Config()
                .listObjectsV2(clientData.getDataRegionEntity().getBucket_name(), prefix);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {

            if (os.getKey().contains(".json")) {
                String[] key = os.getKey().split("/");
                jsonFile.add(key[key.length - 1]);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(jsonFile);
    }

    @PostMapping(value = "/table-real-rule-json/{client_id}/{streamname}/{tablename}")
    public ResponseEntity<?> storeRuleJson(@RequestBody String form, @PathVariable("client_id") String client_id,
            @PathVariable("streamname") String streamname, @PathVariable("tablename") String tablename,
            HttpServletRequest request, HttpServletResponse response) {
        try {

            DepClientEntity client = this.depClientDbService.getClientById(client_id);

            if (client == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            String bucketName;
            String fileName;

            bucketName = client.getDataRegionEntity().getBucket_name();
            String file = tablename.split("_")[0];
            fileName = client.getClient_name() + "/" + streamname + "/" + tablename + "/Rule_engine/Rule/" + file
                    + ".json";

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = client.getDataRegionEntity().getData_region_code();

            PutObjectResult result = AWSS3Configuration.amazonS3Config().putObject(bucketName, fileName, form);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete-table-rule/{clientId}/{batchname}/{tablename}")
    public ResponseEntity<?> deleteTableRule(@PathVariable("clientId") String clientId,
            @PathVariable("batchname") String batchname, @PathVariable("tablename") String tablename) {

        final boolean isDelete = depTableRulesDbService.DeleteTableRule(clientId, batchname, tablename);
        if (isDelete) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // need to develop
    @PostMapping(value = "/upload-json")
    public ResponseEntity<?> uploadJson(@ModelAttribute UploadJsonRequest formData, @RequestParam("path") String path,
            @RequestParam("client_id") String client_id) {

        try {
            DepClientEntity client = this.depClientDbService.getClientById(client_id);
            String bucketName = client.getDataRegionEntity().getBucket_name();

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(formData.getFile().getContentType());
            metaData.setContentLength(formData.getFile().getSize());

            String fileName = path + "/Rule_engine/Table-Json/" + formData.getFile().getOriginalFilename();

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = client.getDataRegionEntity().getData_region_code();

            PutObjectResult objectResult = AWSS3Configuration.amazonS3Config().putObject(
                    client.getDataRegionEntity().getBucket_name(), fileName,
                    formData.getFile().getInputStream(), null);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    @PostMapping(value = "/upload-json-and-store")
    public ResponseEntity<?> uploadNStore(@ModelAttribute UploadJsonRequest formData,
            @RequestParam("client_id") String client_id,
            @RequestParam("batchname") String batchname,
            @RequestParam("tablename") String tablename,
            @RequestParam("path") String path) {

        try {
            DepClientEntity client = this.depClientDbService.getClientById(client_id);
            String bucketName = client.getDataRegionEntity().getBucket_name();

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(formData.getFile().getContentType());
            metaData.setContentLength(formData.getFile().getSize());

            String fileName = path + "/Rule_engine/Table-Json/" + formData.getFile().getOriginalFilename();

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = client.getDataRegionEntity().getData_region_code();

            PutObjectResult objectResult = AWSS3Configuration.amazonS3Config().putObject(
                    client.getDataRegionEntity().getBucket_name(), fileName,
                    formData.getFile().getInputStream(), null);

            depTableRulesDbService.getS3FileNStore(client_id, batchname, tablename, fileName);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    /*
     * API to fetch all the s3 folder of given prefix
     */

    @GetMapping(value = "/get-all-s3-data-list/{client_id}/{client_name}/{batch_name}/{table_name}/{data_region_code}/{bucket_name}")
    public ResponseEntity<?> getS3List(@PathVariable("client_id") String client_id,
            @PathVariable("client_name") String client_name,
            @PathVariable("batch_name") String batch_name,
            @PathVariable("table_name") String table_name,
            @PathVariable("data_region_code") String data_region_code,
            @PathVariable("bucket_name") String bucket_name) {
        try {
            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = data_region_code;

            List<JSONObject> list = Utility.getAllS3List(bucket_name,
                    AWSS3Configuration.amazonS3Config(), client_name, batch_name, table_name,
                    "tablerule");

            return ResponseEntity.status(HttpStatus.OK).body(list.toString());
        } catch (Exception exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
        }
    }

    /*
     * API to fetch all the s3 files of particular folder of given prefix
     */

    @GetMapping(value = "/get-each-s3-data-list/{client_id}")
    public ResponseEntity<?> getS3ParticularListData(@RequestParam String prefix,
            @PathVariable("client_id") String client_id) {
        try {
            DepClientEntity client = this.depClientDbService.getClientById(client_id);

            AWSS3Configuration.awsAccessKey = dynamodbAccessKey;
            AWSS3Configuration.awsSecretKey = dynamodbSecretKey;
            AWSS3Configuration.sessionToken = dynamodbSessionToken;
            AWSS3Configuration.awsRegion = client.getDataRegionEntity().getData_region_code();

            List<JSONObject> list = Utility.getEachS3List(client.getDataRegionEntity().getBucket_name(),
                    AWSS3Configuration.amazonS3Config(), prefix);

            return ResponseEntity.status(HttpStatus.OK).body(list.toString());
        } catch (Exception exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
        }
    }

    @GetMapping("/get-table-rule-with-status/{client_id}/{batchname}")
    public ResponseEntity<List<RuleEngineResponseDto>> getTableRulesWithStatus(
            @PathVariable("client_id") String client_id,
            @PathVariable("batchname") String batchname, HttpServletRequest request, HttpServletResponse response) {
        try {
            final List<RuleEngineResponseDto> list = tableRuleService.getTableRulesWithStatus(client_id, batchname);
            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-table-rule-status/{id}/{execution_id}")
    public ResponseEntity<?> getTableStatus(@PathVariable("id") String id,
            @PathVariable(value = "execution_id") String execution_id, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            final RuleEngineResponseDto list = tableRuleService.getJobStatus(id, execution_id);
            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to get table rule by ID
     */

    @GetMapping("/get-table-rule-by-id/{table_id}")
    public ResponseEntity<RuleEngineResponseDto> getTableRuleById(@PathVariable("table_id") String table_id,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            final RuleEngineResponseDto rule = tableRuleService.getTableRuleById(table_id);

            if (rule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(rule);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
