package com.hospital.appointment.service;

import com.hospital.appointment.entity.DoctorSchedule;
import com.hospital.appointment.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务类
 */
@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleMapper scheduleMapper;
    
    /**
     * 获取医生排班列表
     */
    public List<DoctorSchedule> getScheduleByDoctor(Long doctorId) {
        return scheduleMapper.selectByDoctor(doctorId);
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
