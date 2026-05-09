package com.hospital.appointment.service;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.DoctorSchedule;
import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.appointment.mapper.DoctorMapper;
import com.hospital.appointment.mapper.PatientMapper;
import com.hospital.appointment.mapper.ScheduleMapper;
import com.hospital.appointment.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 挂号预约服务类
 */
@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private ScheduleMapper scheduleMapper;
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private PatientMapper patientMapper;
    
    /**
     * 创建挂号
     */
    @Transactional
    public String createAppointment(Appointment appointment) {
        // 检查患者是否在黑名单
        Patient patient = patientMapper.selectById(appointment.getPatientID());
        if (patient != null && patient.getIsBlacklist() == 1) {
            return null;
        }
        
        // 检查号源
        DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleID());
        if (schedule == null || schedule.getRemainingSlots() <= 0) {
            return null;
        }
        
        // 获取医生和科室信息
        appointment.setDoctorID(schedule.getDoctorID());
        appointment.setDepartmentID(schedule.getDepartmentID());
        
        // 生成挂号ID和序号
        appointment.setAppointmentID(UUIDGenerator.generateAppointmentID());
        appointment.setAppointmentNumber(schedule.getRegisteredSlots() + 1);
        
        // 设置初始状态
        appointment.setAppointmentStatus(1); // 已预约
        appointment.setPaymentStatus(1); // 已支付（模拟）
        appointment.setPaymentAmount(schedule.getPrice());
        appointment.setPaymentTime(LocalDateTime.now());
        appointment.setIsReviewed(0);
        appointment.setCreateTime(LocalDateTime.now());
        
        // 插入挂号记录
        int result = appointmentMapper.insert(appointment);
        if (result > 0) {
            // 更新号源
            scheduleMapper.decrementSlots(appointment.getScheduleID());
            return appointment.getAppointmentID();
        }
        return null;
    }
    
    /**
     * 获取患者的所有挂号记录
     */
    public List<Appointment> getAppointmentsByPatient(Long patientId, Integer status) {
        return appointmentMapper.selectByPatient(patientId, status);
    }
    
    /**
     * 获取医生的所有挂号记录
     */
    public List<Appointment> getAppointmentsByDoctor(Long doctorId, Integer status) {
        return appointmentMapper.selectByDoctor(doctorId, status);
    }
    
    /**
     * 获取挂号详情
     */
    public Appointment getAppointmentById(String id) {
        return appointmentMapper.selectById(id);
    }
    
    /**
     * 取消挂号
     */
    @Transactional
    public boolean cancelAppointment(String id, String reason) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        
        // 检查是否可以取消（规则：就诊前2小时不可取消）
        LocalDateTime appointmentTime = LocalDateTime.of(
            appointment.getAppointmentDate(),
            appointment.getTimeSlot() == 1 ? java.time.LocalTime.of(8, 0) : java.time.LocalTime.of(14, 0)
        );
        
        appointment.setAppointmentStatus(5); // 已取消
        appointment.setCancelReason(reason);
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setPaymentStatus(2); // 已退款
        
        boolean success = appointmentMapper.update(appointment) > 0;
        if (success) {
            // 释放号源
            scheduleMapper.incrementSlots(appointment.getScheduleID());
        }
        return success;
    }
    
    /**
     * 签到
     */
    public boolean checkIn(String id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setAppointmentStatus(2); // 已签到
        return appointmentMapper.update(appointment) > 0;
    }
    
    /**
     * 支付成功
     */
    public boolean paySuccess(String id, String paymentMethod) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setPaymentStatus(1);
        appointment.setPaymentMethod(paymentMethod);
        appointment.setPaymentTime(LocalDateTime.now());
        return appointmentMapper.update(appointment) > 0;
    }
    
    /**
     * 确认就诊
     */
    public boolean confirmVisit(String id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setAppointmentStatus(3); // 就诊中
        return appointmentMapper.update(appointment) > 0;
    }
    
    /**
     * 完成就诊
     */
    public boolean finishVisit(String id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setAppointmentStatus(4); // 已完成
        return appointmentMapper.update(appointment) > 0;
    }
    
    /**
     * 获取当日挂号列表
     */
    public List<Appointment> getTodayAppointments(Long doctorId) {
        return appointmentMapper.selectTodayByDoctor(doctorId, LocalDate.now());
    }
    
    /**
     * 获取明日挂号数
     */
    public long getTomorrowAppointmentCount(Long doctorId) {
        return appointmentMapper.countByDoctorAndDate(doctorId, LocalDate.now().plusDays(1));
    }
}
