package com.hospital.appointment.service;

import com.hospital.appointment.entity.Notification;
import com.hospital.appointment.mapper.HospitalNotificationMapper;
import com.hospital.appointment.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HospitalNotificationService {

    @Autowired
    private HospitalNotificationMapper notificationMapper;

    public List<Notification> getAll() {
        return notificationMapper.selectAll();
    }

    public List<Notification> getLatest(int limit) {
        return notificationMapper.selectLatest(limit);
    }

    public List<Notification> getByFilter(Long patientId, Integer type) {
        return notificationMapper.selectByFilter(patientId, type);
    }

    public Notification getById(String id) {
        return notificationMapper.selectById(id);
    }

    public boolean publish(Notification notification) {
        notification.setNotificationID(UUIDGenerator.generateNotificationID());
        notification.setNotificationType(notification.getNotificationType() != null ? notification.getNotificationType() : 6);
        notification.setNotificationStatus(1);
        notification.setSendTime(LocalDateTime.now());
        return notificationMapper.insert(notification) > 0;
    }

    public boolean markRead(String id) {
        return notificationMapper.markRead(id, 2) > 0;
    }

    public boolean delete(String id) {
        return notificationMapper.deleteById(id) > 0;
    }
}
