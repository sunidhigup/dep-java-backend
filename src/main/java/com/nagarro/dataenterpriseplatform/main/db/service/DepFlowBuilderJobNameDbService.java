package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderEdgesEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderFormEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobInputEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderNodesEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepClientRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderEdgesRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderFormRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobInputRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobNameRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderNodesRepo;
import com.nagarro.dataenterpriseplatform.main.dto.CopyJobDto;

@Service
@Transactional
public class DepFlowBuilderJobNameDbService {
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    @Value("${aws.flowBuilderJsonLocation}")
    private String awsFlowBuilderJsonLocation;

    @Autowired
    private DepFlowBuilderJobNameRepo depFlowBuilderJobNameRepo;

    @Autowired
    private DepFlowBuilderFormRepo depFlowBuilderFormRepo;

    @Autowired
    private DepFlowBuilderFormDbService depFlowBuilderFormDbService;

    @Autowired
    private DepFlowBuilderJobInputRepo depFlowBuilderJobInputRepo;

    @Autowired
    private DepClientRepo depClientRepo;

    @Autowired
    private DepFlowBuilderEdgesRepo depFlowBuilderEdgesRepo;

    @Autowired
    private DepFlowBuilderNodesRepo depFlowBuilderNodesRepo;

    public boolean addJob(DepFlowBuilderJobNameEntity job, String bucket_name) {

        DepFlowBuilderJobNameEntity fetchedJobs = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_idAndInput_ref_key(job.getClient_id(), job.getBatch_id(),
                        job.getInput_ref_key());

        if (fetchedJobs != null) {
            return true;
        } else {
            String extracts = job.getClient_name() + "/" + job.getBatch_name() + "/" + job.getInput_ref_key() + "/"
                    + awsFlowBuilderJsonLocation + "/" + job.getInput_ref_key() + ".json";
            job.setExtracts(extracts);
            // job.setBucket(awsBucketName);
            job.setBucket(bucket_name);
            this.depFlowBuilderJobNameRepo.save(job);
            return false;
        }
    }

    public List<DepFlowBuilderJobNameEntity> getAllJobs(String client_id, String batch_id) {

        List<DepFlowBuilderJobNameEntity> fetchedJobs = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_id(client_id, batch_id);

        if (fetchedJobs.size() == 0)
            return null;

        return fetchedJobs;
    }

    public DepFlowBuilderJobNameEntity getJobById(String id) {
        return this.depFlowBuilderJobNameRepo.findById(id).orElse(null);
    }

    public boolean deleteCurrentJob(String client_id, String batch_id, String job) {

        DepFlowBuilderJobNameEntity fetchedJob = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_idAndInput_ref_key(client_id, batch_id, job);

        if (fetchedJob == null)
            return false;
        else {
            this.depFlowBuilderJobNameRepo.delete(fetchedJob);
            return true;
        }
    }

    public boolean enableJob(String client_id, String batch_id, String job) {

        DepFlowBuilderJobNameEntity fetchedJob = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_idAndInput_ref_key(client_id, batch_id, job);

        if (fetchedJob == null)
            return false;
        else {

            fetchedJob.set_active(true);
            this.depFlowBuilderJobNameRepo.save(fetchedJob);
            return true;
        }
    }

    public boolean disableJob(String client_id, String batch_id, String job) {
        DepFlowBuilderJobNameEntity fetchedJob = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_idAndInput_ref_key(client_id, batch_id, job);

        if (fetchedJob == null)
            return false;
        else {

            fetchedJob.set_active(false);
            this.depFlowBuilderJobNameRepo.save(fetchedJob);
            return true;
        }
    }

    public void copyNewJob(CopyJobDto job, String bucket_name) {
        DepFlowBuilderJobNameEntity newJob = new DepFlowBuilderJobNameEntity();
        newJob.setClient_name(job.getClient_name());
        newJob.setBatch_id(job.getBatch_id());
        newJob.setClient_id(job.getClient_id());
        newJob.setBatch_name(job.getBatch_name());
        newJob.setInput_ref_key(job.getInput_ref_key());
        addJob(newJob, bucket_name);

        DepFlowBuilderFormEntity list = this.depFlowBuilderFormRepo
                .findByClient_idAndBatch_idAndJobname(job.getCopyClientId(), job.getCopyBatchId(), job.getCopyJob());
        if (list != null) {
            DepFlowBuilderFormEntity newFlowForm = new DepFlowBuilderFormEntity();
            String uniqueID = UUID.randomUUID().toString();
            newFlowForm.setFieldId(uniqueID);
            newFlowForm.setBatch(job.getBatch_name());
            newFlowForm.setClient_id(job.getClient_id());
            newFlowForm.setBatch_id(job.getBatch_id());
            newFlowForm.setJobName(job.getInput_ref_key());
            newFlowForm.setNodes(list.getNodes());
            this.depFlowBuilderFormDbService.createNewFormData(newFlowForm);
        }
        if (list == null)
            return;

        DepFlowBuilderJobInputEntity jobInput = this.depFlowBuilderJobInputRepo
                .findByClient_idAndBatch_idAndJob(job.getCopyClientId(), job.getCopyBatchId(), job.getCopyJob());

        if (jobInput != null) {
            DepFlowBuilderJobInputEntity newJobInput = new DepFlowBuilderJobInputEntity();
            String extracts = job.getClient_name() + "/" + job.getBatch_name() + "/" + job.getInput_ref_key() + "/" + awsFlowBuilderJsonLocation + "/"+ job.getInput_ref_key() + ".json";
            String uniqueID = UUID.randomUUID().toString();
            newJobInput.setUniqueId(uniqueID);
            newJobInput.setJob(job.getInput_ref_key());
            newJobInput.setExtract(extracts);
            newJobInput.setClient_id(job.getClient_id());
            newJobInput.setBatch_id(job.getBatch_id());
            newJobInput.setBucket(jobInput.getBucket());
            newJobInput.setPrefix(jobInput.getPrefix());
            this.depFlowBuilderJobInputRepo.save(newJobInput);
        }

        DepFlowBuilderNodesEntity nodes = this.depFlowBuilderNodesRepo
                .findByClient_idAndBatch_idAndJobName(job.getCopyClientId(), job.getCopyBatchId(), job.getCopyJob());

        if (nodes != null) {
            DepFlowBuilderNodesEntity newNodes = new DepFlowBuilderNodesEntity();
            String uniqueID = UUID.randomUUID().toString();
            newNodes.setFieldId(uniqueID);
            newNodes.setBatch(job.getBatch_name());
            newNodes.setJobName(job.getInput_ref_key());
            newNodes.setClient_id(job.getClient_id());
            newNodes.setBatch_id(job.getBatch_id());
            newNodes.setNodes(nodes.getNodes());
            this.depFlowBuilderNodesRepo.save(newNodes);
        }

        DepFlowBuilderEdgesEntity edges = this.depFlowBuilderEdgesRepo
                .findByClient_idAndBatch_idAndJobName(job.getCopyClientId(), job.getCopyBatchId(), job.getCopyJob());

        if (edges != null) {
            DepFlowBuilderEdgesEntity newEdges = new DepFlowBuilderEdgesEntity();
            String uniqueID = UUID.randomUUID().toString();
            newEdges.setFieldId(uniqueID);
            newEdges.setBatch(job.getBatch_name());
            newEdges.setJobName(job.getInput_ref_key());
            newEdges.setClient_id(job.getClient_id());
            newEdges.setBatch_id(job.getBatch_id());
            newEdges.setEdges(edges.getEdges());
            this.depFlowBuilderEdgesRepo.save(newEdges);
        }

        DepClientEntity copyClient = this.depClientRepo.findById(job.getCopyClientId()).orElse(null);
        assert copyClient != null;
        copyFlowbuilderJson(job.getClient_name(), job.getBatch_name(), job.getInput_ref_key(), job.getCopyJob(),
                job.getCopyBatch(), copyClient.getClient_name());
    }

    public void copyFlowbuilderJson(String Client, String batch, String job, String copyJob, String copyBatch,
            String copyClient) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(awsBucketName)
                .withPrefix(
                        copyClient + "/" + copyBatch + "/" + copyJob + "/" + awsFlowBuilderJsonLocation + "/" + copyJob)
                .withDelimiter("/");

        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);

        String destKey = Client + "/" + batch + "/" + job + "/" + awsFlowBuilderJsonLocation + "/" + job + ".json";
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            amazonS3.copyObject(awsBucketName, objectSummary.getKey(), awsBucketName, destKey);
        }
    }

    public boolean UpdateJob(DepFlowBuilderJobNameEntity job) {
        try {

            DepFlowBuilderJobNameEntity fetchedJob = this.depFlowBuilderJobNameRepo
                    .findByClient_idAndBatch_idAndInput_ref_key(job.getClient_id(), job.getBatch_id(),
                            job.getInput_ref_key());

            if (fetchedJob != null) {
                this.depFlowBuilderJobNameRepo.delete(fetchedJob);
            }

            this.depFlowBuilderJobNameRepo.save(job);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DepFlowBuilderJobNameEntity> getApprovedJobs(String client_id, String batch_id) {
        List<DepFlowBuilderJobNameEntity> fetchedJobs = this.depFlowBuilderJobNameRepo
                .findByClient_idAndBatch_id(client_id, batch_id);

        if (fetchedJobs.size() == 0)
            return null;

        List<DepFlowBuilderJobNameEntity> approvedJobs = new ArrayList<>();
        for (DepFlowBuilderJobNameEntity job : fetchedJobs) {
            if (job.getStatus().equals("approved")) {
                approvedJobs.add(job);
            }
        }

        if (approvedJobs.size() == 0)
            return null;

        return approvedJobs;
    }

    public boolean setJobRunType(DepFlowBuilderJobNameEntity job) {
        try {
            DepFlowBuilderJobNameEntity fetchedJob = this.depFlowBuilderJobNameRepo.findById(job.getJob_id())
                    .orElse(null);

            if (fetchedJob != null) {
                fetchedJob.setConnectionType(job.getConnectionType());
                fetchedJob.setConnectionName(job.getConnectionName());
            }

            this.depFlowBuilderJobNameRepo.save(fetchedJob);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DepFlowBuilderJobNameEntity> getAllJobs() {
        return this.depFlowBuilderJobNameRepo.findAll();
    }
}
