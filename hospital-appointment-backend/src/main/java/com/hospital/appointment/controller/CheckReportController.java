package com.hospital.appointment.controller;

import com.hospital.appointment.entity.CheckReport;
import com.hospital.appointment.service.CheckReportService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital/report")
@CrossOrigin(origins = "*")
public class CheckReportController {

    @Autowired
    private CheckReportService checkReportService;

    @GetMapping("/patient/{patientId}")
    public Result<List<CheckReport>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String reportType) {
        List<CheckReport> list = checkReportService.getByPatient(patientId, reportType);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<CheckReport> getById(@PathVariable Long id) {
        CheckReport report = checkReportService.getById(id);
        if (report != null) {
            return Result.success(report);
        }
        return Result.error("报告不存在");
    }
}
