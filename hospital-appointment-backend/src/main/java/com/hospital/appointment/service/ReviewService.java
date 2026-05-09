package com.hospital.appointment.service;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.entity.Review;
import com.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.appointment.mapper.ReviewMapper;
import com.hospital.appointment.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价服务类
 */
@Service
public class ReviewService {
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    /**
     * 提交评价
     */
    @Transactional
    public boolean submitReview(Review review) {
        review.setReviewID(UUIDGenerator.generateReviewID());
        review.setReviewStatus(1); // 待审核
        review.setCreateTime(LocalDateTime.now());
        
        boolean success = reviewMapper.insert(review) > 0;
        if (success) {
            // 更新挂号记录的评价状态
            appointmentMapper.updateIsReviewed(review.getAppointmentID(), 1);
        }
        return success;
    }
    
    /**
     * 获取医生的评价列表
     */
    public List<Review> getReviewsByDoctor(Long doctorId, Integer page, Integer size) {
        return reviewMapper.selectByDoctor(doctorId, (page - 1) * size, size);
    }
    
    /**
     * 获取患者的历史评价
     */
    public List<Review> getReviewsByPatient(Long patientId) {
        return reviewMapper.selectByPatient(patientId);
    }
    
    /**
     * 获取评价详情
     */
    public Review getReviewById(String id) {
        return reviewMapper.selectById(id);
    }
    
    /**
     * 获取医生平均评分
     */
    public double getAverageRating(Long doctorId) {
        Double rating = reviewMapper.selectAverageRating(doctorId);
        return rating != null ? rating : 0.0;
    }
    
    /**
     * 医生回复评价
     */
    public boolean replyReview(String id, String reply) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            return false;
        }
        review.setReplyContent(reply);
        review.setReplyTime(LocalDateTime.now());
        return reviewMapper.update(review) > 0;
    }
    
    /**
     * 审核评价
     */
    public boolean auditReview(String id, Integer status) {
        return reviewMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 获取待审核评价
     */
    public List<Review> getPendingReviews() {
        return reviewMapper.selectPending();
    }
}
