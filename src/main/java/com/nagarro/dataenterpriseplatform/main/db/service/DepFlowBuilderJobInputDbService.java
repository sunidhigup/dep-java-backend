package com.nagarro.dataenterpriseplatform.main.db.service;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobInputEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobInputRepo;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderJobNameRepo;
import com.nagarro.dataenterpriseplatform.main.dto.JobInputExtractUpdateDto;
import com.nagarro.dataenterpriseplatform.main.dto.ReadFormDto;
import com.nagarro.dataenterpriseplatform.main.dto.ReadNodesDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DepFlowBuilderJobInputDbService {

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.flowBuilderJsonLocation}")
	private String awsFlowBuilderJsonLocation;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private DepFlowBuilderJobInputRepo depFlowBuilderJobInputRepo;

	@Autowired
	private DepFlowBuilderJobNameRepo depFlowBuilderJobNameRepo;

	public DepFlowBuilderJobInputEntity getJobInput(String client_id, String batch_id, String job) {
		DepFlowBuilderJobInputEntity fetchedJobInput = this.depFlowBuilderJobInputRepo
				.findByClient_idAndBatch_idAndJob(client_id, batch_id, job);
		return fetchedJobInput;
	}

	public DepFlowBuilderJobInputEntity fetchJobInput(String client_id, String batch_id, String jobname) {
		return this.depFlowBuilderJobInputRepo.findById(batch_id + "_" + jobname).orElse(null);
	}

	public List<DepFlowBuilderJobNameEntity> fetchJob(String client_id, String batch_id, String batchname, String job) {
		return this.depFlowBuilderJobNameRepo.findByClient_idAndBatch_idAndBatch_nameAndInput_ref_key(client_id,
				batch_id, batchname, job);
	}

	public void insertJobInput(String client_id, String batch_id, String jobname, String batchname,
			ReadNodesDto formData) {

		DepFlowBuilderJobInputEntity jobInputs = fetchJobInput(client_id, batch_id, jobname);

		if (jobInputs != null) {
			depFlowBuilderJobInputRepo.delete(jobInputs);
		}

		for (ReadFormDto readForm : formData.getReadForms()) {

			DepFlowBuilderJobInputEntity fetchedInputs = fetchJobInput(client_id, batch_id, jobname);

			if (fetchedInputs == null) {
				DepFlowBuilderJobInputEntity input = new DepFlowBuilderJobInputEntity();

				ArrayList<String> path = new ArrayList<>();
				path.addAll(List.of(readForm.getFormField().getPath().split("/")));

				path.remove(0);
				path.remove(0);
				String bucket = path.remove(0);

				path.remove(path.size() - 1);
				String prefix = String.join("/", path);

				String extension = readForm.getFormField().getFormat();
				String job = jobname;
				String batch = batchname;

				List<DepFlowBuilderJobNameEntity> fetchedJob = fetchJob(client_id, batch_id, batch, job);
				String extracts = fetchedJob.get(0).getExtracts();

				if (extracts.contains(job)) {
					input.setExtract(extracts);
				}

				input.setClient_id(client_id);
				input.setBatch_id(batch_id);
				input.setBucket(bucket);
				input.setExtensions(extension);
				input.setJob(job);
				input.setPrefix(prefix);
				input.setUniqueId(batch_id + "_" + job);

				this.depFlowBuilderJobInputRepo.save(input);
			} else {
				DepFlowBuilderJobInputEntity inp = fetchedInputs;

				DepFlowBuilderJobInputEntity input = new DepFlowBuilderJobInputEntity();

				ArrayList<String> path = new ArrayList<>();
				path.addAll(List.of(readForm.getFormField().getPath().split("/")));

				path.remove(0);
				path.remove(0);
				String bucket = path.remove(0);

				path.remove(path.size() - 1);
				String prefix = String.join("/", path);

				String extension = readForm.getFormField().getFormat();
				String job = jobname;
				String batch = batchname;

				List<DepFlowBuilderJobNameEntity> fetchedJob = fetchJob(client_id, batch_id, batch, job);
				String extracts = fetchedJob.get(0).getExtracts();

				if (extracts.contains(job)) {
					input.setExtract(extracts);
				}

				input.setClient_id(client_id);
				input.setBatch_id(batch_id);
				input.setBucket(inp.getBucket());
				input.setExtensions(inp.getExtensions() + "," + extension);
				input.setJob(inp.getJob());
				input.setPrefix(inp.getPrefix() + "," + prefix);
				input.setUniqueId(batch_id + "_" + inp.getJob());
				this.depFlowBuilderJobInputRepo.save(input);
			}
		}
	}

	public void deleteJobInput(String client_id, String batch_id, String jobname) {
		DepFlowBuilderJobInputEntity fetchedInputs = fetchJobInput(client_id, batch_id, jobname);
		if (fetchedInputs != null) {
			this.depFlowBuilderJobInputRepo.delete(fetchedInputs);
		}
	}

	public void deleteJsonFromS3(String batch, String job) {
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(awsBucketName)
					.withPrefix(awsFlowBuilderJsonLocation).withDelimiter("/");

			ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);

			List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();

			for (S3ObjectSummary objectSummary : summaries) {
				if (objectSummary.getKey().contains(job)) {
					amazonS3.deleteObject(awsBucketName, objectSummary.getKey());
				}
			}
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
		}
	}

	public boolean UpdateExtractJob(JobInputExtractUpdateDto job) {
		try {
			final DepFlowBuilderJobInputEntity fetchedJobsInput = depFlowBuilderJobInputRepo
					.findByClient_idAndBatch_idAndJob(job.getClient_id(), job.getBatch_id(), job.getJob_name());
			if (fetchedJobsInput == null) {
				return false;
			} else {
				if (job.getTracking_id().equals("snowflake-garbageTID")) {
					fetchedJobsInput.setExtract(job.getClient_name() + "/" + job.getBatch_name() + "/"
							+ job.getJob_name() + "/" + awsFlowBuilderJsonLocation + "/" + job.getFilename() + ".json");
				} else {
					fetchedJobsInput.setExtract(job.getClient_name() + "/" + job.getBatch_name() + "/"
							+ job.getJob_name() + "/" + awsFlowBuilderJsonLocation + "/" + job.getTracking_id() + "/"
							+ job.getFilename() + ".json");
				}
				depFlowBuilderJobInputRepo.save(fetchedJobsInput);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
