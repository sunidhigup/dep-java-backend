package com.nagarro.dataenterpriseplatform.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oracle-info")
@CrossOrigin(origins = "*")
public class OracleDbController {

    @Autowired
    private Environment env;

    @GetMapping(value = "/get-db-list")
    public ResponseEntity<?> getDbnames() {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        try (Connection connection =
                     DriverManager.getConnection(env.getProperty("aws.oracle.url"),
                             env.getProperty("aws.oracle.username"), env.getProperty("aws.oracle.password"))) {

            List<Map<String,String>> dbNameList = new ArrayList<>();
            Statement stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery("SELECT * FROM DBA_USERS");

            while(result.next()){
                Map<String,String> db = new HashMap<>();
                db.put("label",result.getString(1));
                dbNameList.add(db);
            }
            connection.close();

            if(dbNameList.size() == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(dbNameList);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/get-table-list/{dbname}")
    public ResponseEntity<?> getTableListNames(@PathVariable("dbname") String dbname) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try (Connection connection =
                     DriverManager.getConnection(env.getProperty("aws.oracle.url"),
                             env.getProperty("aws.oracle.username"), env.getProperty("aws.oracle.password"))) {


            Statement stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery("SELECT table_name, owner FROM dba_tables WHERE owner='" + dbname + "' ORDER BY owner, table_name");

            List<Map<String,String>> tableNameList = new ArrayList<>();

            while (result.next()) {
                Map<String,String> table = new HashMap<>();
                table.put("label",result.getString(1));
                tableNameList.add(table);

            }

            if(tableNameList.size() == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(tableNameList);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping(value = "/get-column-list/{dbname}/{tablename}")
    public ResponseEntity<?> getColumns(@PathVariable("dbname") String dbname, @PathVariable("tablename") String tablename) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try(Connection connection =  DriverManager.getConnection(env.getProperty("aws.oracle.url"),
                env.getProperty("aws.oracle.username"), env.getProperty("aws.oracle.password"))) {

            DatabaseMetaData dbmd = connection.getMetaData();

            ResultSet result = dbmd.getColumns(null, null, tablename, null);

            List<String> columnNameList = new ArrayList<>();

            while (result.next()) {
                columnNameList.add(result.getString("COLUMN_NAME"));
            }

            if(columnNameList.size() == 0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(columnNameList);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
