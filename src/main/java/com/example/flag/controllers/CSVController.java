package com.example.flag.controllers;

import com.example.flag.services.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CSVController {

    @Autowired
    CSVService userMessageService;

    @PostMapping("/csv")
    public ResponseEntity<String> processCSV(@RequestBody String csvData) {
        String result = userMessageService.parseCSV(csvData);
        return ResponseEntity.ok(result);
    }
}
