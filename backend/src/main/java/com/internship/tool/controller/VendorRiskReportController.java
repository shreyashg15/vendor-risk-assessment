package com.internship.tool.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.internship.tool.service.VendorRiskReportService;
import java.util.Map;

@RestController
@RequestMapping("/api/vendors/risk")
public class VendorRiskReportController {

    @Autowired
    private VendorRiskReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getRiskReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}
