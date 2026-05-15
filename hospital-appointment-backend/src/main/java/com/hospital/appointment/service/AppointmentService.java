package com.hospital.appointment.service;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.DoctorSchedule;
import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.exception.AppointmentException;
import com.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.appointment.mapper.DoctorMapper;
import com.hospital.appointment.mapper.PatientMapper;
import com.hospital.appointment.mapper.ScheduleMapper;
import com.hospital.appointment.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        expireTimeoutAppointments();

        // 检查患者是否在黑名单
        Patient patient = patientMapper.selectById(appointment.getPatientID());
        if (patient != null && patient.getIsBlacklist() == 1) {
            throw new AppointmentException("患者已在黑名单中，无法预约");
        }
        
        // 检查号源
        DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleID());
        if (schedule == null) {
            throw new AppointmentException("排班不存在");
        }
        if (schedule.getScheduleStatus() == null || schedule.getScheduleStatus() != 1 || schedule.getRemainingSlots() <= 0) {
            throw new AppointmentException("号源已满或当前排班不可预约");
        }

        Appointment conflict = appointmentMapper.selectByPatientAndSlot(
            appointment.getPatientID(), schedule.getScheduleDate(), schedule.getTimeSlot()
        );
        if (conflict != null) {
            throw new AppointmentException("同一患者在同一时间段不能重复预约");
        }
        
        // 获取医生和科室信息
        appointment.setDoctorID(schedule.getDoctorID());
        appointment.setDepartmentID(schedule.getDepartmentID());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setTimeSlot(schedule.getTimeSlot());
        
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
            if (scheduleMapper.decrementSlots(appointment.getScheduleID()) <= 0) {
                throw new AppointmentException("号源已被抢完，请刷新后重试");
            }
            return appointment.getAppointmentID();
        }
        throw new AppointmentException("挂号失败，请稍后重试");
    }
    
    /**
     * 获取患者的所有挂号记录
     */
    public List<Appointment> getAppointmentsByPatient(Long patientId, Integer status) {
        expireTimeoutAppointments();
        return appointmentMapper.selectByPatient(patientId, status);
    }
    
    /**
     * 获取医生的所有挂号记录
     */
    public List<Appointment> getAppointmentsByDoctor(Long doctorId, Integer status) {
        expireTimeoutAppointments();
        return appointmentMapper.selectByDoctor(doctorId, status);
    }
    
    /**
     * 获取挂号详情
     */
    public Appointment getAppointmentById(String id) {
        expireTimeoutAppointments();
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment != null && appointment.getAppointmentStatus() != null && appointment.getAppointmentStatus() == 1 && isCheckInExpired(appointment)) {
            appointmentMapper.updateStatus(appointment.getAppointmentID(), 8);
            appointment.setAppointmentStatus(8);
        }
        return appointment;
    }
    
    /**
     * 取消挂号
     */
    @Transactional
    public boolean cancelAppointment(String id, String reason) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new AppointmentException("挂号记录不存在");
        }
        if (appointment.getAppointmentStatus() == null || appointment.getAppointmentStatus() != 1) {
            throw new AppointmentException("当前状态不可取消");
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
            throw new AppointmentException("挂号记录不存在");
        }
        if (appointment.getAppointmentStatus() == null || appointment.getAppointmentStatus() != 1) {
            throw new AppointmentException("当前状态不可签到");
        }
        if (isCheckInExpired(appointment)) {
            appointmentMapper.updateStatus(appointment.getAppointmentID(), 8);
            throw new AppointmentException("已超过签到时限，挂号已失效");
        }
        return appointmentMapper.updateStatus(appointment.getAppointmentID(), 2) > 0;
    }
    
    /**
     * 支付成功
     */
    public boolean paySuccess(String id, String paymentMethod) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new AppointmentException("挂号记录不存在");
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
            throw new AppointmentException("挂号记录不存在");
        }
        if (appointment.getAppointmentStatus() == null || appointment.getAppointmentStatus() != 2) {
            throw new AppointmentException("当前状态不可接诊");
        }
        return appointmentMapper.updateStatus(appointment.getAppointmentID(), 3) > 0;
    }
    
    /**
     * 完成就诊
     */
    public boolean finishVisit(String id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new AppointmentException("挂号记录不存在");
        }
        if (appointment.getAppointmentStatus() == null || appointment.getAppointmentStatus() != 3) {
            throw new AppointmentException("当前状态不可完成就诊");
        }
        return appointmentMapper.updateStatus(appointment.getAppointmentID(), 4) > 0;
    }
    
    /**
     * 获取当日挂号列表
     */
    public List<Appointment> getTodayAppointments(Long doctorId) {
        expireTimeoutAppointments();
        return appointmentMapper.selectTodayByDoctor(doctorId, LocalDate.now());
    }
    
    /**
     * 获取明日挂号数
     */
    public long getTomorrowAppointmentCount(Long doctorId) {
        return appointmentMapper.countByDoctorAndDate(doctorId, LocalDate.now().plusDays(1));
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireTimeoutAppointments() {
        List<Appointment> timeoutAppointments = appointmentMapper.selectTimeoutAppointments();
        for (Appointment appointment : timeoutAppointments) {
            appointmentMapper.updateStatus(appointment.getAppointmentID(), 8);
        }
    }

    private boolean isCheckInExpired(Appointment appointment) {
        LocalDateTime startTime = LocalDateTime.of(appointment.getAppointmentDate(), resolveAppointmentStartTime(appointment));
        return LocalDateTime.now().isAfter(startTime.plusMinutes(5));
    }

    private LocalTime resolveAppointmentStartTime(Appointment appointment) {
        if (appointment.getScheduleStartTime() != null) {
            return appointment.getScheduleStartTime();
        }
        if (appointment.getTimeSlot() != null) {
            switch (appointment.getTimeSlot()) {
                case 1:
                    return LocalTime.of(8, 0);
                case 2:
                    return LocalTime.of(14, 0);
                case 3:
                    return LocalTime.of(19, 0);
                default:
                    break;
            }
        }
        return LocalTime.of(8, 0);
    }
}
