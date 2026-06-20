package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Notification;
import com.hospital.appointment.service.HospitalNotificationService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital/notification")
@CrossOrigin(origins = "*")
public class HospitalNotificationController {

    @Autowired
    private HospitalNotificationService notificationService;

    @GetMapping("/all")
    public Result<List<Notification>> getAll() {
        return Result.success(notificationService.getAll());
    }

    @GetMapping("/latest")
    public Result<List<Notification>> getLatest(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(notificationService.getLatest(limit));
    }

    @GetMapping("/filter")
    public Result<List<Notification>> getByFilter(@RequestParam(required = false) Long patientId,
                                                   @RequestParam(required = false) Integer type) {
        return Result.success(notificationService.getByFilter(patientId, type));
    }

    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable String id) {
        Notification n = notificationService.getById(id);
        return n != null ? Result.success(n) : Result.error("通知不存在");
    }

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Notification notification) {
        boolean ok = notificationService.publish(notification);
        return ok ? Result.success("发布成功") : Result.error("发布失败");
    }

    @PutMapping("/{id}/read")
    public Result<String> markRead(@PathVariable String id) {
        boolean ok = notificationService.markRead(id);
        return ok ? Result.success("标记已读") : Result.error("操作失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable String id) {
        boolean ok = notificationService.delete(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }
}
