package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.service.AppointmentService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 挂号预约控制器
 * 改造自 ReservationController
 */
@RestController
@RequestMapping("/api/hospital/appointment")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;

    /**
     * 获取所有挂号记录（管理员）
     */
    @GetMapping("/admin/all")
    public Result<List<Appointment>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return Result.success(list);
    }
    
    /**
     * 创建挂号
     */
    @PostMapping
    public Result<String> createAppointment(@RequestBody Appointment appointment) {
        String appointmentID = appointmentService.createAppointment(appointment);
        if (appointmentID != null) {
            return Result.success(appointmentID);
        }
        return Result.error("挂号失败，可能号源已满");
    }
    
    /**
     * 获取患者的所有挂号记录
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId,
                                                               @RequestParam(required = false) Integer status) {
        List<Appointment> list = appointmentService.getAppointmentsByPatient(patientId, status);
        return Result.success(list);
    }
    
    /**
     * 获取医生的所有挂号记录
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<Appointment>> getAppointmentsByDoctor(@PathVariable Long doctorId,
                                                              @RequestParam(required = false) Integer status) {
        List<Appointment> list = appointmentService.getAppointmentsByDoctor(doctorId, status);
        return Result.success(list);
    }
    
    /**
     * 获取挂号详情
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointmentById(@PathVariable String id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return Result.success(appointment);
        }
        return Result.error("挂号记录不存在");
    }
    
    /**
     * 取消挂号
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelAppointment(@PathVariable String id, 
                                            @RequestParam(required = false) String reason) {
        boolean success = appointmentService.cancelAppointment(id, reason);
        if (success) {
            return Result.success("取消成功");
        }
        return Result.error("取消失败，已过退号时间");
    }
    
    /**
     * 签到
     */
    @PutMapping("/{id}/checkin")
    public Result<String> checkIn(@PathVariable String id) {
        boolean success = appointmentService.checkIn(id);
        if (success) {
            return Result.success("签到成功");
        }
        return Result.error("签到失败");
    }
    
    /**
     * 支付成功回调
     */
    @PutMapping("/{id}/pay")
    public Result<String> paySuccess(@PathVariable String id, 
                                      @RequestParam String paymentMethod) {
        boolean success = appointmentService.paySuccess(id, paymentMethod);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败");
    }
    
    /**
     * 确认就诊
     */
    @PutMapping("/{id}/confirm")
    public Result<String> confirmVisit(@PathVariable String id) {
        boolean success = appointmentService.confirmVisit(id);
        if (success) {
            return Result.success("确认就诊成功");
        }
        return Result.error("确认就诊失败");
    }
    
    /**
     * 完成就诊
     */
    @PutMapping("/{id}/finish")
    public Result<String> finishVisit(@PathVariable String id) {
        boolean success = appointmentService.finishVisit(id);
        if (success) {
            return Result.success("就诊完成");
        }
        return Result.error("操作失败");
    }
    
    /**
     * 获取当日挂号列表
     */
    @GetMapping("/today")
    public Result<List<Appointment>> getTodayAppointments(@RequestParam Long doctorId) {
        List<Appointment> list = appointmentService.getTodayAppointments(doctorId);
        return Result.success(list);
    }
    
    /**
     * 获取明日挂号数
     */
    @GetMapping("/tomorrow/count")
    public Result<Long> getTomorrowAppointmentCount(@RequestParam Long doctorId) {
        long count = appointmentService.getTomorrowAppointmentCount(doctorId);
        return Result.success(count);
    }
}
