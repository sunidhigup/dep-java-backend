package com.nagarro.dataenterpriseplatform.main.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ClientService {

	List<LinkedHashMap<String, Object>> getInfo(String userId);
}
