package com.hospital.appointment.service;

import com.hospital.appointment.entity.MedicalRecord;
import com.hospital.appointment.entity.Prescription;
import com.hospital.appointment.exception.AppointmentException;
import com.hospital.appointment.mapper.MedicalRecordMapper;
import com.hospital.appointment.mapper.PrescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门诊病历服务
 */
@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    /**
     * 获取患者的病历列表
     */
    public List<MedicalRecord> getRecordsByPatient(Long patientId) {
        return medicalRecordMapper.selectByPatient(patientId);
    }

    /**
     * 获取病历详情（含处方）
     */
    public Map<String, Object> getRecordDetail(Long recordId) {
        MedicalRecord record = medicalRecordMapper.selectById(recordId);
        if (record == null) {
            throw new AppointmentException("病历不存在");
        }
        List<Prescription> prescriptions = prescriptionMapper.selectByRecordId(recordId);

        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("prescriptions", prescriptions);
        return result;
    }

    /**
     * 创建或更新病历
     */
    public MedicalRecord saveRecord(MedicalRecord record) {
        if (record.getRecordID() != null) {
            // 更新
            medicalRecordMapper.update(record);
            return medicalRecordMapper.selectById(record.getRecordID());
        } else {
            // 新建
            record.setCreateTime(java.time.LocalDateTime.now());
            record.setUpdateTime(java.time.LocalDateTime.now());
            medicalRecordMapper.insert(record);
            return record;
        }
    }
}
