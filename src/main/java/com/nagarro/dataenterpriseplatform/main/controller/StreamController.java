package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;

import com.nagarro.dataenterpriseplatform.main.dto.RealTimeResponseDto;
import com.nagarro.dataenterpriseplatform.main.service.RealTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ListStreamsRequest;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepStreamMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepStreamMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.dto.DataStreamDto;

@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "*")
public class StreamController {

    @Autowired
    private DepStreamMetadataDbService depStreamMetadataDbService;

    @Autowired
    private AmazonKinesis akClient;

    @Autowired
    private RealTimeService realTimeService;

    @Autowired
    private DepClientDbService depClientDbService;

    @Value("${region_name}")
    private String region;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    @PostMapping("/create-stream")
    public ResponseEntity<?> addStream(@RequestBody DepStreamMetadataEntity stream,
            @RequestParam String client_id,
            @RequestParam String user_id) {

        DepClientEntity getClient = depClientDbService.getDepClientByClientIdAndUserId(client_id, user_id);
        stream.setBucket(getClient.getDataRegionEntity().getBucket_name());
        stream.setRegion(getClient.getDataRegionEntity().getData_region_code());

        // stream.setBucket(awsBucketName);
        // stream.setRegion(region);

        boolean exist = depStreamMetadataDbService.addStream(stream);

        if (!exist) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // list all kinesis stream data

    @GetMapping("/kinesis/get-all-stream")
    public ResponseEntity<?> getAllDataStream() {

        ListStreamsRequest listStreamsRequest = new ListStreamsRequest();

        ListStreamsResult listStreamsResult = akClient.listStreams(listStreamsRequest);
        List<String> streamNames = listStreamsResult.getStreamNames();

        return ResponseEntity.status(HttpStatus.OK).body(streamNames);
    }

    /*
     * API for fetching all stream through meta table
     */

    @GetMapping("/get-all-stream")
    public ResponseEntity<?> getAllStreams() {
        final List<DepStreamMetadataEntity> list = depStreamMetadataDbService.getAllStreams();
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/get-by-name/{client_name}")
    public ResponseEntity<?> getByStreamByClientName(@PathVariable String client_name) {
        final List<DepStreamMetadataEntity> list = depStreamMetadataDbService.getStreamByClientName(client_name);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/get-by-stream-name/{stream_name}")
    public ResponseEntity<?> getStreamByStreamName(@PathVariable String stream_name) {
        final DepStreamMetadataEntity stream = depStreamMetadataDbService.getStreamByStreamName(stream_name);
        if (stream == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(stream);
    }

    @PostMapping("/new-data-stream")
    public ResponseEntity<?> createNewDataStream(@RequestBody DataStreamDto dataStream) {

        CreateStreamRequest request = new CreateStreamRequest();
        request.setStreamName(dataStream.getDataStreamName());
        request.setShardCount(dataStream.getDataStreamSize());

        akClient.createStream(request);

        DescribeStreamRequest describeRequest = new DescribeStreamRequest();
        describeRequest.setStreamName(dataStream.getDataStreamName());
        DescribeStreamResult describeStreamResponse = akClient.describeStream(describeRequest);
        String streamStatus = describeStreamResponse.getStreamDescription().getStreamStatus();

        return ResponseEntity.status(HttpStatus.CREATED).body(streamStatus);

    }

    @GetMapping("/get-all-stream/{client_name}")
    public ResponseEntity<?> getAllStreamsByClientName(@PathVariable String client_name) {
        final List<RealTimeResponseDto> list = realTimeService.getAllStreamsAndStatus(client_name);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/get-stream-status")
    public ResponseEntity<?> listCluster(@RequestParam(required = false) String clientName,
            @RequestParam(required = false) String streamName, @RequestParam(required = false) String clusterId,
            @RequestParam(required = false) String clusterName) {
        final RealTimeResponseDto list = realTimeService.clusterStatus(clientName, streamName, clusterName, clusterId);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
