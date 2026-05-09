package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Review;
import com.hospital.appointment.service.ReviewService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 * 改造自 FeedBackController
 */
@RestController
@RequestMapping("/api/hospital/review")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    /**
     * 提交评价
     */
    @PostMapping
    public Result<String> submitReview(@RequestBody Review review) {
        boolean success = reviewService.submitReview(review);
        if (success) {
            return Result.success("评价提交成功");
        }
        return Result.error("评价提交失败");
    }
    
    /**
     * 获取医生的评价列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<Review>> getReviewsByDoctor(@PathVariable Long doctorId,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        List<Review> list = reviewService.getReviewsByDoctor(doctorId, page, size);
        return Result.success(list);
    }
    
    /**
     * 获取患者的历史评价
     */
    @GetMapping("/patient/{patientId}")
    public Result<List<Review>> getReviewsByPatient(@PathVariable Long patientId) {
        List<Review> list = reviewService.getReviewsByPatient(patientId);
        return Result.success(list);
    }
    
    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    public Result<Review> getReviewById(@PathVariable String id) {
        Review review = reviewService.getReviewById(id);
        if (review != null) {
            return Result.success(review);
        }
        return Result.error("评价不存在");
    }
    
    /**
     * 获取医生的平均评分
     */
    @GetMapping("/doctor/{doctorId}/rating")
    public Result<Double> getAverageRating(@PathVariable Long doctorId) {
        double rating = reviewService.getAverageRating(doctorId);
        return Result.success(rating);
    }
    
    /**
     * 医生回复评价
     */
    @PutMapping("/{id}/reply")
    public Result<String> replyReview(@PathVariable String id, @RequestParam String reply) {
        boolean success = reviewService.replyReview(id, reply);
        if (success) {
            return Result.success("回复成功");
        }
        return Result.error("回复失败");
    }
    
    /**
     * 审核评价（管理员）
     */
    @PutMapping("/{id}/audit")
    public Result<String> auditReview(@PathVariable String id, @RequestParam Integer status) {
        boolean success = reviewService.auditReview(id, status);
        if (success) {
            return Result.success("审核成功");
        }
        return Result.error("审核失败");
    }
    
    /**
     * 获取待审核评价列表（管理员）
     */
    @GetMapping("/pending")
    public Result<List<Review>> getPendingReviews() {
        List<Review> list = reviewService.getPendingReviews();
        return Result.success(list);
    }
}
