package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.service.ClientService;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientController {

	@Autowired
	DepClientDbService depClientDbService;

	@Autowired
	private ClientService clientService;

	@PostMapping("/create-client")
	public ResponseEntity<?> createClient(@RequestBody DepClientEntity client) {
		boolean exist = this.depClientDbService.saveClient(client);

		if (exist) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/*
	 * API for fetching all client
	 */

	@GetMapping("/get-all-client")
	public ResponseEntity<?> getClient(HttpServletRequest request) {

		List<DepClientEntity> list = this.depClientDbService.getAllClient();

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/get-approved-client")
	public ResponseEntity<?> getApprovedClient(HttpServletRequest request) {

		List<DepClientEntity> list = this.depClientDbService.getClientByApprovedStatus();

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@PutMapping("/update-client-detail")
	public ResponseEntity<?> updateClientDetail(
			@RequestBody DepClientEntity client,
			HttpServletRequest request,
			HttpServletResponse response) {

		try {
			this.depClientDbService.updateClient(client);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/get-by-name/{client_name}")
	public ResponseEntity<?> getClientByName(@PathVariable String client_name) {

		List<DepClientEntity> list = this.depClientDbService.getClientByName(client_name);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/get-by-id/{id}")
	public ResponseEntity<?> getClientById(@PathVariable String id) {

		DepClientEntity list = this.depClientDbService.getClientById(id);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@DeleteMapping("/delete-client-detail")
	public ResponseEntity<?> deleteClient(@RequestParam String client_id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean res = this.depClientDbService.deleteClientById(client_id);

			if (res) {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	@GetMapping("/get-info/{user_id}")
	public ResponseEntity<?> getInfo(@PathVariable String user_id) {

		final List<LinkedHashMap<String, Object>> list = clientService.getInfo(user_id);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	// role based user access

	@GetMapping("/get-client-by-userid/{userId}")
	public List<DepClientEntity> getClientByUserId(@PathVariable String userId) {
		List<DepClientEntity> clients = this.depClientDbService.getClientByUserId(userId);

		if (clients.size() > 0)
			return clients;
		else
			return null;

	}

	@PostMapping("/create-client/{id}")
	public ResponseEntity<?> createClientForUser(@PathVariable String id, @RequestBody DepClientEntity clientEntity) {

		boolean isPersist = this.depClientDbService.saveClientForUser(id, clientEntity);

		if (isPersist)
			return ResponseEntity.status(HttpStatus.CREATED).build();
		else
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@GetMapping("/get-approved-client/{userId}")
	public ResponseEntity<?> getApprovedClient(@PathVariable String userId, HttpServletRequest request) {

		List<DepClientEntity> list = this.depClientDbService.getClientByApprovedStatus(userId);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@GetMapping("/get-client-by-adminId/{adminId}")
	public ResponseEntity<?> getClientByAdminID(@PathVariable String adminId, HttpServletRequest request) {

		List<DepClientEntity> list = this.depClientDbService.getClientByAdminId(adminId);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	// updateClientForUser
	@PutMapping("/update-client-detail/{userId}")
	public ResponseEntity<?> updateClientDetailForUser(@RequestBody DepClientEntity client,
			@PathVariable(name = "userId") String id, HttpServletRequest request, HttpServletResponse response) {

		boolean isPersist = this.depClientDbService.saveClientForUser(id, client);

		if (isPersist)
			return ResponseEntity.status(HttpStatus.CREATED).build();
		else
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@PostMapping("/approve-client/{email}/{clientid}")
	public ResponseEntity<?> ApproveClient(@PathVariable String email, @PathVariable String clientid,
			HttpServletRequest request) {
		try {
			DepClientEntity res = this.depClientDbService.approveClientByUserEmail(email, clientid);

			if (res == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

			}
			return ResponseEntity.status(HttpStatus.OK).body(res);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}

	}

}
