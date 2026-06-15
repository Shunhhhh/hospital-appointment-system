package com.hospital.appointment.controller;

import com.hospital.appointment.entity.MedicalRecord;
import com.hospital.appointment.service.MedicalRecordService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 门诊病历控制器
 */
@RestController
@RequestMapping("/api/hospital/medical-record")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * 获取患者的病历列表
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalRecord>> getRecordsByPatient(@PathVariable Long patientId) {
        List<MedicalRecord> list = medicalRecordService.getRecordsByPatient(patientId);
        return Result.success(list);
    }

    /**
     * 获取病历详情（含处方）
     */
    @GetMapping("/{recordId}")
    public Result<Map<String, Object>> getRecordDetail(@PathVariable Long recordId) {
        try {
            Map<String, Object> detail = medicalRecordService.getRecordDetail(recordId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 保存病历（新建或更新）
     */
    @PostMapping
    public Result<MedicalRecord> saveRecord(@RequestBody MedicalRecord record) {
        try {
            MedicalRecord saved = medicalRecordService.saveRecord(record);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
