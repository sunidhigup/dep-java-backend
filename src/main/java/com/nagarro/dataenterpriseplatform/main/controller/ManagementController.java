package com.nagarro.dataenterpriseplatform.main.controller;

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

import com.nagarro.dataenterpriseplatform.main.db.entity.DepManagementDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepManagementDetailsDbService;

@RestController
@RequestMapping("/api/management-details")
@CrossOrigin(origins = "*")
public class ManagementController {

	@Autowired
	private DepManagementDetailsDbService managementService;

	/*
	 * API to get all the management employee users
	 */

	@GetMapping("/fetch-management-employees-details")
	public ResponseEntity<List<DepManagementDetailsEntity>> getAllEmployees(HttpServletRequest request,
																			HttpServletResponse response) {
		final List<DepManagementDetailsEntity> employeesList = managementService.getManagementEmployees();
		if (employeesList.size() == 0)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.status(HttpStatus.OK).body(employeesList);
	}

	@GetMapping("/userDetailInfo")
	public ResponseEntity<DepManagementDetailsEntity> getUserDetaiInfo(@RequestParam String username,
																	   HttpServletRequest request, HttpServletResponse response) {
		DepManagementDetailsEntity employee = this.managementService.GetUserDetaiInfo(username);
		if (employee == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.status(HttpStatus.OK).body(employee);
	}

	// get user by adminID

	@GetMapping("/get-user-by-adminId/{adminid}")
	public ResponseEntity<List<DepManagementDetailsEntity>> getUserByAdminId(@PathVariable String adminid,
																			 HttpServletRequest request, HttpServletResponse response) {
		List<DepManagementDetailsEntity> users = this.managementService.findUserByAdminId(adminid);
		if (users == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	@PostMapping("/grant-access/{clientId}")
	public ResponseEntity<?> grantClientAccess(@PathVariable String clientId, @RequestBody List<String> userIds) {
		this.managementService.grantClientAccessToUsers(clientId, userIds);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/*
	 * API to approve employee details by admin
	 */

	@PostMapping("/approve-employee-detail/{userId}")
	public ResponseEntity<?> approveEmployee(@RequestBody DepManagementDetailsEntity emp, @PathVariable String userId,
											 HttpServletRequest request, HttpServletResponse response) {

		boolean empExist = this.managementService.approveManagementEmployee(emp, userId);

		if (!empExist) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	/*
	 * API to update the management employee details
	 */

	@PutMapping("/update-management-employee-detail")
	public ResponseEntity<?> updateManagementEmployeeDetail(@RequestBody DepManagementDetailsEntity emp,
															HttpServletRequest request, HttpServletResponse response) {
		try {
			this.managementService.updateManagementEmployee(emp);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/*
	 * API to delete from management Table
	 */

	@DeleteMapping("/delete-management-employee-detail/{email}")
	public ResponseEntity<?> deleteEmployee(@PathVariable("email") String email, HttpServletRequest request,
											HttpServletResponse response) {
		this.managementService.deleteManagementEmployee(email);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
