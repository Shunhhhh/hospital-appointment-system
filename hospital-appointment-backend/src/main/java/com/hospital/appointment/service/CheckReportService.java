package com.hospital.appointment.service;

import com.hospital.appointment.entity.CheckReport;
import com.hospital.appointment.mapper.CheckReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckReportService {

    @Autowired
    private CheckReportMapper checkReportMapper;

    public List<CheckReport> getByPatient(Long patientId, String reportType) {
        if (reportType != null && !reportType.isEmpty()) {
            return checkReportMapper.selectByPatientAndType(patientId, reportType);
        }
        return checkReportMapper.selectByPatient(patientId);
    }

    public CheckReport getById(Long id) {
        return checkReportMapper.selectById(id);
    }
}
