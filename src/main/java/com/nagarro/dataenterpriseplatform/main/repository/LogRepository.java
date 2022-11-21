package com.nagarro.dataenterpriseplatform.main.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowbuilderJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepPreProcessorFileClassificationStatusDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepRuleEngineJobStatusDbService;

@Repository
public class LogRepository {

	@Autowired
	private DepRuleEngineJobStatusDbService depRuleEngineJobStatusDbService;
	
	
	@Autowired
	private DepFlowbuilderJobStatusDbService depFlowbuilderJobStatusDbService;
	
	@Autowired
	private DepPreProcessorFileClassificationStatusDbService depPreProcessorFileClassificationStatusDbService;
	


    public Object GetLogStatusById(String id,String jobName) {
    	Object statusObj=null;

        if(jobName.equalsIgnoreCase("ruleEngine")) {
        	statusObj=depRuleEngineJobStatusDbService.getLogStatusById(id);
        	return statusObj;
        }
        
        else if(jobName.equalsIgnoreCase("preprocessor")) {
        	statusObj =depPreProcessorFileClassificationStatusDbService.getPreProcessor(id);
        	return statusObj;
        }
        
        else if(jobName.equals("dataProcessor")) {
        	DepFlowbuilderJobStatusEntity statusTable=depFlowbuilderJobStatusDbService.getLogStatusById(id);
        	return statusTable;
        }
        else {
        	return null;
        }
        
    }
}
