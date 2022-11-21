package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMDMEntityMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepMDMEntityMetadataService;

@RestController
@RequestMapping("/api/entity")
public class MDMEntityController {

	@Autowired
	DepMDMEntityMetadataService entityRepository;

	@PostMapping("/create-entity")
	public ResponseEntity<?> createNewEntity(@RequestBody DepMDMEntityMetadataEntity entity) {

		boolean isCreated = this.entityRepository.createEntity(entity);

		if (!isCreated)
			return ResponseEntity.status(HttpStatus.CREATED).build();

		else
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping("/get-all-entities")
	public ResponseEntity<?> getAllEntities() {

		List<DepMDMEntityMetadataEntity> entities = this.entityRepository.getAllEntity();

		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

	@GetMapping("/get-entity/{entityId}")
	public ResponseEntity<?> getByEntityId(@PathVariable String entityId) {

		DepMDMEntityMetadataEntity entity = this.entityRepository.getEntityById(entityId);

		return ResponseEntity.status(HttpStatus.OK).body(entity);

	}

	@DeleteMapping("delete-entity/{entityId}")
	public ResponseEntity<?> deleteEntitybyId(@PathVariable String entityId) {
		boolean isDeleted = this.entityRepository.deleteEntityById(entityId);

		return ResponseEntity.status(HttpStatus.OK).build();

	}
}
