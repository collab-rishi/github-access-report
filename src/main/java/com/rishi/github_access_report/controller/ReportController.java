package com.rishi.github_access_report.controller;


import com.rishi.github_access_report.dto.UserAccessReportDTO;
import com.rishi.github_access_report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{orgName}")
    public ResponseEntity<List<UserAccessReportDTO>> getOrReport(@PathVariable String orgName) {

        List<UserAccessReportDTO> report = reportService.getAggregatedReport(orgName);
        return ResponseEntity.ok(report);
    }


}
