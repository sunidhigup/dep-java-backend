package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nagarro.dataenterpriseplatform.main.db.entity.UserDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.UserDetailsDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;


@RestController
@RequestMapping("/api/employee-details")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private UserDetailsDbService employeeRepository;

    @Value("${aws.cognito.clientId}")
    private String clientID;

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    /*
     * API to fetch all employee details 
     */

    @GetMapping("/fetch-employees-details")
    public ResponseEntity<List<UserDetailsEntity>> getAllEmployees(HttpServletRequest request, HttpServletResponse response) {
        List<UserDetailsEntity> employeesList = this.employeeRepository.getEmployees();
        if (employeesList.size() == 0)
            return ResponseEntity.status(HttpStatus.OK).build();
        System.out.println(employeesList);
        return ResponseEntity.status(HttpStatus.OK).body(employeesList);
    }

    /*
     * API to fetch single employee details from dynamo with key email : no use
     * till now
     */

    @GetMapping("/fetch-employee-detail/{email}")
    public ResponseEntity<?> getOneEmployee(@PathVariable("email") String email, HttpServletRequest request,
            HttpServletResponse response) {
        try {
        	final UserDetailsEntity emp = this.employeeRepository.fetchEmployee(email);

            if (emp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(emp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * API to delete the employee details by admin from table = "user-seignup"
     */

    @DeleteMapping("/delete-employee-detail/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") String id, HttpServletRequest request,
            HttpServletResponse response) {

        this.employeeRepository.deleteEmployee(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
