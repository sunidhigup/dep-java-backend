package com.nagarro.dataenterpriseplatform.main.db.service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPredictiveModelsEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepPredictiveModelsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DepPredictiveModelsDbService {

    @Autowired
    private DepPredictiveModelsRepo depPredictiveModelsRepo;

    public List<DepPredictiveModelsEntity> getAllSgements() {
        return this.depPredictiveModelsRepo.findAll();
    }
}
