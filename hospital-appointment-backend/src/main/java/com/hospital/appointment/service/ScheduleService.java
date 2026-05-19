package com.hospital.appointment.service;

import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.entity.DoctorSchedule;
import com.hospital.appointment.mapper.DoctorMapper;
import com.hospital.appointment.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排班服务类
 */
@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleMapper scheduleMapper;
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    /**
     * 获取所有排班
     */
    public List<DoctorSchedule> getAllSchedules() {
        List<DoctorSchedule> list = scheduleMapper.selectAll();
        // 动态修正过期或已约满的状态，避免前端显示过期日期仍为可预约
        LocalDate today = LocalDate.now();
        for (DoctorSchedule ds : list) {
            if (ds.getScheduleDate() != null) {
                if (ds.getScheduleDate().isBefore(today)) {
                    ds.setScheduleStatus(0); // 已过期/不可预约
                    continue;
                }
            }
            // 如果剩余号源为0，则标记为已约满
            if (ds.getRemainingSlots() == null || ds.getRemainingSlots() <= 0) {
                ds.setScheduleStatus(2);
            }
        }
        return list;
    }

    /**
     * 获取医生排班列表
     */
    public List<DoctorSchedule> getScheduleByDoctor(Long doctorId) {
        List<DoctorSchedule> list = scheduleMapper.selectByDoctor(doctorId);
        // 如果没有排班数据，自动生成默认排班
        if (list.isEmpty()) {
            generateDefaultSchedules(doctorId);
            list = scheduleMapper.selectByDoctor(doctorId);
        }
        return list;
    }
    
    /**
     * 为所有医生生成未来7天默认排班
     */
    public boolean generateAllDefaultSchedules() {
        List<Doctor> doctors = doctorMapper.selectAll();
        for (Doctor doctor : doctors) {
            generateDefaultSchedules(doctor.getDoctorID());
        }
        return true;
    }
    
    /**
     * 为指定医生生成未来7天默认排班（周一到周五，上下午各一个时段）
     */
    private void generateDefaultSchedules(Long doctorId) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) return;
        
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            // 跳过周六周日
            if (date.getDayOfWeek().getValue() >= 6) continue;
            
            // 检查该日期是否已有排班
            List<DoctorSchedule> existing = scheduleMapper.selectByDoctorAndDate(doctorId, date);
            if (!existing.isEmpty()) continue;
            
            // 上午时段 08:00-12:00
            DoctorSchedule morning = new DoctorSchedule();
            morning.setDoctorID(doctorId);
            morning.setScheduleDate(date);
            morning.setTimeSlot(1);
            morning.setStartTime(LocalTime.of(8, 0));
            morning.setEndTime(LocalTime.of(12, 0));
            morning.setTotalSlots(20);
            morning.setRemainingSlots(20);
            morning.setRegisteredSlots(0);
            morning.setPrice(doctor.getRegistrationFee() != null ? doctor.getRegistrationFee() : BigDecimal.valueOf(20));
            morning.setRegistrationType(doctor.getTitle() != null && doctor.getTitle().contains("主任") ? 2 : 1);
            morning.setScheduleStatus(1);
            scheduleMapper.insert(morning);
            
            // 下午时段 14:00-18:00
            DoctorSchedule afternoon = new DoctorSchedule();
            afternoon.setDoctorID(doctorId);
            afternoon.setScheduleDate(date);
            afternoon.setTimeSlot(2);
            afternoon.setStartTime(LocalTime.of(14, 0));
            afternoon.setEndTime(LocalTime.of(18, 0));
            afternoon.setTotalSlots(20);
            afternoon.setRemainingSlots(20);
            afternoon.setRegisteredSlots(0);
            afternoon.setPrice(doctor.getRegistrationFee() != null ? doctor.getRegistrationFee() : BigDecimal.valueOf(20));
            afternoon.setRegistrationType(doctor.getTitle() != null && doctor.getTitle().contains("主任") ? 2 : 1);
            afternoon.setScheduleStatus(1);
            scheduleMapper.insert(afternoon);
        }
    }
    
    /**
     * 获取指定日期的排班
     */
    public List<DoctorSchedule> getScheduleByDate(LocalDate date) {
        return scheduleMapper.selectByDate(date);
    }
    
    /**
     * 获取科室下所有医生的排班
     */
    public List<DoctorSchedule> getScheduleByDepartment(Long departmentId, LocalDate startDate, LocalDate endDate) {
        return scheduleMapper.selectByDepartment(departmentId, startDate, endDate);
    }
    
    /**
     * 获取可用号源
     */
    public List<DoctorSchedule> getAvailableSchedules(Long doctorId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        return scheduleMapper.selectAvailable(doctorId, departmentId, startDate, endDate);
    }
    
    /**
     * 获取排班详情
     */
    public DoctorSchedule getScheduleById(Long id) {
        return scheduleMapper.selectById(id);
    }
    
    /**
     * 创建排班
     */
    public boolean addSchedule(DoctorSchedule schedule) {
        return scheduleMapper.insert(schedule) > 0;
    }
    
    /**
     * 批量创建排班
     */
    public boolean addScheduleBatch(List<DoctorSchedule> schedules) {
        for (DoctorSchedule schedule : schedules) {
            scheduleMapper.insert(schedule);
        }
        return true;
    }
    
    /**
     * 更新排班
     */
    public boolean updateSchedule(DoctorSchedule schedule) {
        return scheduleMapper.update(schedule) > 0;
    }
    
    /**
     * 删除排班
     */
    public boolean deleteSchedule(Long id) {
        return scheduleMapper.deleteById(id) > 0;
    }
    
    /**
     * 停诊
     */
    public boolean stopSchedule(Long id) {
        DoctorSchedule ds = scheduleMapper.selectById(id);
        if (ds == null) return false;
        // 不允许对已过日期的排班进行停诊
        if (ds.getScheduleDate() != null && ds.getScheduleDate().isBefore(LocalDate.now())) {
            return false;
        }
        return scheduleMapper.updateStatus(id, 0) > 0;
    }
    
    /**
     * 预约号源减一
     */
    public boolean decrementSlots(Long id) {
        return scheduleMapper.decrementSlots(id) > 0;
    }
    
    /**
     * 释放号源加一
     */
    public boolean incrementSlots(Long id) {
        return scheduleMapper.incrementSlots(id) > 0;
    }
}
