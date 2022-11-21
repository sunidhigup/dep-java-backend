package com.nagarro.dataenterpriseplatform.main.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/postgres-info")
@CrossOrigin(origins = "*")
public class PostresDbController {
    @Autowired
    private Environment env;

    @GetMapping(value = "/get-db-list")
    public ResponseEntity<?> getDbnames() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        try (Connection connection = DriverManager.getConnection(env.getProperty("aws.postgres.url"),
                env.getProperty("aws.postgres.username"), env.getProperty("aws.postgres.password"))) {

            List<Map<String, String>> dbNameList = new ArrayList<>();
            Statement stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery("SELECT datname FROM pg_database;");

            while (result.next()) {
                Map<String, String> db = new HashMap<>();
                db.put("label", result.getString(1));
                dbNameList.add(db);
            }
            connection.close();

            if (dbNameList.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(dbNameList);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/get-schema-list/{dbname}")
    public ResponseEntity<?> getSchemaListNames(@PathVariable("dbname") String dbname) {

        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try (Connection connection = DriverManager.getConnection(
                env.getProperty("aws.postgres.dburi") + dbname + env.getProperty("aws.postgres.dbstringtype"),
                env.getProperty("aws.postgres.username"), env.getProperty("aws.postgres.password"))) {

            Statement stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery(" SELECT * FROM pg_tables");

            Set<Map<String, String>> schemas = new LinkedHashSet();

            while (result.next()) {
                Map<String, String> table = new HashMap<>();
                table.put("label", result.getString(1));
                schemas.add(table);
            }

            if (schemas.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(schemas);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping(value = "/get-table-list/{dbname}/{schema}")
    public ResponseEntity<?> getTableListNames(@PathVariable("dbname") String dbname,
            @PathVariable("schema") String schema) {

        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try (Connection connection = DriverManager.getConnection(
                env.getProperty("aws.postgres.dburi") + dbname + env.getProperty("aws.postgres.dbstringtype"),
                env.getProperty("aws.postgres.username"), env.getProperty("aws.postgres.password"))) {

            Statement stmt = connection.createStatement();

            String Statement = " SELECT * FROM pg_tables";

            ResultSet result = stmt.executeQuery(Statement);

            Set<Map<String, String>> tableList = new LinkedHashSet();

            while (result.next()) {
                Map<String, String> table = new HashMap<>();
                table.put("schema", result.getString(1));
                table.put("table", result.getString(2));
                if (result.getString(1).equals(schema)) {
                    tableList.add(table);
                }
            }

            if (tableList.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(tableList);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
