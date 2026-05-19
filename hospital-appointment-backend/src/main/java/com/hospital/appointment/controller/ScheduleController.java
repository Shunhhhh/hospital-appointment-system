package com.hospital.appointment.controller;

import com.hospital.appointment.entity.DoctorSchedule;
import com.hospital.appointment.service.ScheduleService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班管理控制器
 * 改造自 SeatManageController
 */
@RestController
@RequestMapping("/api/hospital/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取所有排班（管理员）
     */
    @GetMapping("/admin/all")
    public Result<List<DoctorSchedule>> getAllSchedules() {
        List<DoctorSchedule> list = scheduleService.getAllSchedules();
        return Result.success(list);
    }
    
    /**
     * 获取医生排班列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<DoctorSchedule>> getScheduleByDoctor(@PathVariable Long doctorId) {
        List<DoctorSchedule> list = scheduleService.getScheduleByDoctor(doctorId);
        return Result.success(list);
    }
    
    /**
     * 获取指定日期的排班
     */
    @GetMapping("/date/{date}")
    public Result<List<DoctorSchedule>> getScheduleByDate(@PathVariable LocalDate date) {
        List<DoctorSchedule> list = scheduleService.getScheduleByDate(date);
        return Result.success(list);
    }
    
    /**
     * 获取科室下所有医生的排班
     */
    @GetMapping("/department/{departmentId}")
    public Result<List<DoctorSchedule>> getScheduleByDepartment(@PathVariable Long departmentId,
                                                                @RequestParam(required = false) LocalDate startDate,
                                                                @RequestParam(required = false) LocalDate endDate) {
        List<DoctorSchedule> list = scheduleService.getScheduleByDepartment(departmentId, startDate, endDate);
        return Result.success(list);
    }
    
    /**
     * 获取可用号源
     */
    @GetMapping("/available")
    public Result<List<DoctorSchedule>> getAvailableSchedules(@RequestParam(required = false) Long doctorId,
                                                             @RequestParam(required = false) Long departmentId,
                                                             @RequestParam(required = false) LocalDate startDate,
                                                             @RequestParam(required = false) LocalDate endDate) {
        List<DoctorSchedule> list = scheduleService.getAvailableSchedules(doctorId, departmentId, startDate, endDate);
        return Result.success(list);
    }
    
    /**
     * 获取排班详情
     */
    @GetMapping("/{id}")
    public Result<DoctorSchedule> getScheduleById(@PathVariable Long id) {
        DoctorSchedule schedule = scheduleService.getScheduleById(id);
        if (schedule != null) {
            return Result.success(schedule);
        }
        return Result.error("排班不存在");
    }
    
    /**
     * 创建排班
     */
    @PostMapping
    public Result<String> addSchedule(@RequestBody DoctorSchedule schedule) {
        boolean success = scheduleService.addSchedule(schedule);
        if (success) {
            return Result.success("排班创建成功");
        }
        return Result.error("排班创建失败");
    }
    
    /**
     * 批量创建排班
     */
    @PostMapping("/batch")
    public Result<String> addScheduleBatch(@RequestBody List<DoctorSchedule> schedules) {
        boolean success = scheduleService.addScheduleBatch(schedules);
        if (success) {
            return Result.success("批量排班创建成功");
        }
        return Result.error("批量排班创建失败");
    }
    
    /**
     * 更新排班
     */
    @PutMapping("/{id}")
    public Result<String> updateSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        schedule.setScheduleID(id);
        boolean success = scheduleService.updateSchedule(schedule);
        if (success) {
            return Result.success("排班更新成功");
        }
        return Result.error("排班更新失败");
    }
    
    /**
     * 删除排班
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        boolean success = scheduleService.deleteSchedule(id);
        if (success) {
            return Result.success("排班删除成功");
        }
        return Result.error("排班删除失败");
    }
    
    /**
     * 停诊操作
     */
    @PutMapping("/{id}/stop")
    public Result<String> stopSchedule(@PathVariable Long id) {
        // 先检查排班是否存在以及是否已过期，避免对已过日期执行停诊
        DoctorSchedule ds = scheduleService.getScheduleById(id);
        if (ds == null) {
            return Result.error("排班不存在");
        }
        if (ds.getScheduleDate() != null && ds.getScheduleDate().isBefore(LocalDate.now())) {
            return Result.error("不能对已过期的排班停诊");
        }
        boolean success = scheduleService.stopSchedule(id);
        if (success) {
            return Result.success("停诊成功");
        }
        return Result.error("停诊失败");
    }
    
    /**
     * 为所有医生生成默认排班（未来7天工作日，上下午各一个时段）
     */
    @PostMapping("/init")
    public Result<String> initSchedules() {
        boolean success = scheduleService.generateAllDefaultSchedules();
        if (success) {
            return Result.success("排班数据生成成功");
        }
        return Result.error("排班数据生成失败");
    }
    
    /**
     * 预约号源减一
     */
    @PutMapping("/{id}/decrement")
    public Result<String> decrementSlots(@PathVariable Long id) {
        boolean success = scheduleService.decrementSlots(id);
        if (success) {
            return Result.success("号源预约成功");
        }
        return Result.success("号源预约失败");
    }
    
    /**
     * 取消预约号源加一
     */
    @PutMapping("/{id}/increment")
    public Result<String> incrementSlots(@PathVariable Long id) {
        boolean success = scheduleService.incrementSlots(id);
        if (success) {
            return Result.success("号源释放成功");
        }
        return Result.error("号源释放失败");
    }
}
