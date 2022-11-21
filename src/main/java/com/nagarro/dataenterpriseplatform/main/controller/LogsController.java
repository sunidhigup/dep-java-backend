package com.nagarro.dataenterpriseplatform.main.controller;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepEmrEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepEmrService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationStatusDbService;
import com.nagarro.dataenterpriseplatform.main.repository.LogRepository;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Controller class for logs operation in flow builder and rule engine
 * */

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogsController {

    @Value("${aws.dataProcessor.logGroupName}")
    private String dataProcessorLogGroup;

    @Value("${aws.ruleEngine.logGroupName}")
    private String ruleEngineLogGroup;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    @Autowired
    private DepPreProcessorFileClassificationStatusDbService preProcessRepository;

    @Autowired
    private LogRepository logRepository;
    
    @Autowired
	private DepEmrService depEmrService;


    @Autowired
    private AWSLogs awsLogs;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.preProcessor.logGroupName}")
    private String preProcessorLogGrp;

    @Value("${aws.preprocessor.fargateLogGroupName}")
    private String fargateLogGroup;
    
    private static final String underscore="_";
    
    private static final String pdfStructureLogSuffix="structure_Extraction";
    
    private static final String pdfContentLogSuffix="content_Extraction";
    /*
     * API for fetching emr log list
     */

    @GetMapping(value = "/emr-log-list/{batch}/{job}")
    public ResponseEntity<?> getEmrLogList(@PathVariable("batch") String batch, @PathVariable("job") String job,
            HttpServletRequest request) {

        try {
        	final List<DepEmrEntity> list = this.depEmrService.fetchAllEmrList();

            List<Map<String, String>> clusterId = new ArrayList<>();

            if (list.size() > 0) {

                for (DepEmrEntity item : list) {

                    if (item.getJob_names() != null) {
                    	final List<String> jobNames = new ArrayList<String>(Arrays.asList(item.getJob_names().split(",")));
                    	jobNames.forEach((elem) -> {
                            String[] jobsArray = elem.split(",");
                            if (Objects.equals(jobsArray[0], job) && (Objects.equals(jobsArray[1], batch))) {
                                Map<String, String> cluster = new HashMap<>();
                                System.out.println(item.toString());
                                cluster.put("cluster_id", item.getCluster_id());
                                cluster.put("status", item.getStatus());

                                clusterId.add(cluster);
                            }
                        });
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(clusterId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping(value = "/emr-log-list1/{batch}/{job}")
    public ResponseEntity<?> getNewEmrLogList(@PathVariable("batch") String batch, @PathVariable("job") String job,
            HttpServletRequest request) {

        try {
        	List<DepEmrEntity> list = this.depEmrService.fetchAllEmrList();

            List<Map<String, String>> clusterId = new ArrayList<>();

            if (list.size() > 0) {

                for (DepEmrEntity item : list) {

                    if (item.getJob_names() != null) {
                    	final List<String> jobNames = new ArrayList<String>(Arrays.asList(item.getJob_names().split(",")));
                    	jobNames.forEach((elem) -> {
                            String[] jobsArray = elem.split(",");
                            if (Objects.equals(jobsArray[0], job) && (Objects.equals(jobsArray[1], batch))) {
                                Map<String, String> cluster = new HashMap<>();
                                System.out.println(item.toString());
                                cluster.put("cluster_id", item.getCluster_id());
                                cluster.put("status", item.getStatus());

                                clusterId.add(cluster);
                            }
                        });
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(clusterId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API for fetching logs cluster
     */

    @PostMapping("/emr-log-cluster")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getLogCluster(
            @RequestBody Map<String, String> logcluster, HttpServletRequest request) {

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName("cdep")
                .withPrefix(logcluster.get("path"))
                .withDelimiter("/");

        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);

        List<String> commonPrefixes = objectListing.getCommonPrefixes();
        List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();

        List<Map<String, String>> sub_folders = new ArrayList<>();
        List<Map<String, String>> sub_files = new ArrayList<>();
        Map<String, List<Map<String, String>>> sub_directories = new HashMap<>();

        for (S3ObjectSummary objectSummary : summaries) {
            Map<String, String> hs = new HashMap<>();
            hs.put("label", objectSummary.getKey().replace(objectListing.getPrefix(), ""));
            hs.put("value", objectSummary.getKey().replace(objectListing.getPrefix(), ""));
            sub_files.add(hs);
        }

        sub_directories.put("sub_files", sub_files);

        for (String prefixes : commonPrefixes) {
            Map<String, String> hs = new HashMap<>();
            hs.put("label", prefixes.replace(objectListing.getPrefix(), ""));
            hs.put("value", prefixes);
            sub_folders.add(hs);
        }

        sub_directories.put("sub_folders", sub_folders);

        return ResponseEntity.status(HttpStatus.OK).body(sub_directories);
    }

    /*
     * API for fetching logs based on jobtype and prefix
     */

    @GetMapping(value = "/get-prefixlog/{job-type}/{prefix}")
    public ResponseEntity<?> getLogbyPrefix(@PathVariable("job-type") String jobType,
            @PathVariable("prefix") String prefix, HttpServletRequest request) {

        try {
            ArrayList<String> dataProcessor = null;
            String logGrp = null;
            if (jobType.equalsIgnoreCase("preProcessor")) {
                logGrp = preProcessorLogGrp;
            }
            if (jobType.equalsIgnoreCase("ruleEngine")) {
                logGrp = ruleEngineLogGroup;
            }

            if (jobType.equalsIgnoreCase("dataProcessor")) {
                dataProcessor = new ArrayList<String>();
                logGrp = dataProcessorLogGroup;
            }
            

            DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest()
                    .withLogGroupName(logGrp).withLogStreamNamePrefix(prefix).withDescending(true);
            DescribeLogStreamsResult describeLogStreamsResult = awsLogs.describeLogStreams(describeLogStreamsRequest);

            ArrayList<JSONObject> result = new ArrayList<>();

            for (LogStream logStream : describeLogStreamsResult.getLogStreams()) {
                if (logStream.getCreationTime() != null) {
                    JSONObject input = new JSONObject();
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
                    input.put("timestamp", simple.format(new Date(logStream.getCreationTime())));
                    input.put("log_stream_name", logStream.getLogStreamName());
                    result.add(input);
                }

            }
            Collections.sort(result, (o1, o2) -> o2.getAsString("timestamp").compareTo(o1.getAsString("timestamp")));
            return ResponseEntity.status(HttpStatus.OK).body(result.toString());
        } catch (Exception exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
        }
    }

    /*
     * API for fetching logs based on jobtype and log stream name
     */

    @GetMapping(value = "/get-logs/{job-type}/{log-stream}")
    public ResponseEntity<?> getLogsByStream(@PathVariable("job-type") String jobType,
            @PathVariable("log-stream") String logstream,@RequestParam(value = "lambdaLog",defaultValue = "false") Boolean lambdaLog,@RequestParam(value = "structurLog",defaultValue = "false") 
    Boolean structurLog,@RequestParam(value = "contentLog",defaultValue = "false") Boolean contentLog ) {
        try {
            String logGrp = null;
            if (jobType.equalsIgnoreCase("preProcessor")) {
                logGrp = preProcessorLogGrp;
                if(lambdaLog==true) {
                	structurLog=false;
                	contentLog=false;
                	DepPreProcessorFileClassificationStatusEntity preprcsSts=this.preProcessRepository.getPreProcessor(logstream);
                	String ext=preprcsSts.getExtension();
                	logstream+=underscore+ext;
                }
                if(structurLog==true) {
                	lambdaLog=false;
                	 contentLog=false;
                	logstream+=underscore+pdfStructureLogSuffix;
                }
                if(contentLog==true) {
                	lambdaLog=false;
                	structurLog=false;
                	logstream+=underscore+pdfContentLogSuffix;
                }
                
            }
            if (jobType.equalsIgnoreCase("ruleEngine")) {
                logGrp = ruleEngineLogGroup;
            }

            if (jobType.equalsIgnoreCase("dataProcessor")) {
                logGrp = dataProcessorLogGroup;
            }
            GetLogEventsRequest logrequest = new GetLogEventsRequest().withLogGroupName(logGrp)
                    .withLogStreamName(logstream);

            GetLogEventsResult result = awsLogs.getLogEvents(logrequest);

            ArrayList<String> event = new ArrayList<>();

            for (OutputLogEvent logStream : result.getEvents()) {

                event.add(logStream.getMessage());
            }

            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    /*
     * API to fetch the status if through jobName
     */

    @GetMapping("/{jobname}/status")
    public ResponseEntity<?> getLogStatusByStreamExecutionId(@PathVariable("jobname") String jobName,
            @RequestParam String id,
            HttpServletRequest request) {
        try {
            Object status = this.logRepository.GetLogStatusById(id, jobName);
            if (status == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
