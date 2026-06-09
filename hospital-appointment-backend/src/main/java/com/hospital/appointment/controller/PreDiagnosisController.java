package com.hospital.appointment.controller;

import com.hospital.appointment.service.PreDiagnosisService;
import com.hospital.appointment.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 预问诊控制器
 */
@RestController
@RequestMapping("/api/hospital/pre-diagnosis")
@CrossOrigin(origins = "*")
public class PreDiagnosisController {

    private static final Logger log = LoggerFactory.getLogger(PreDiagnosisController.class);

    @Autowired
    private PreDiagnosisService preDiagnosisService;

    /**
     * 单轮对话：输入症状，返回 AI 建议
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> request) {
        String symptom = request.get("symptom");
        if (symptom == null || symptom.trim().isEmpty()) {
            return Result.error("请输入症状描述");
        }
        try {
            String reply = preDiagnosisService.chat(symptom);
            Result<String> result = Result.success();
            result.setData(reply);
            return result;
        } catch (Exception e) {
            log.error("预问诊异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 多轮对话：携带历史消息
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/chat/history")
    public Result<String> chatWithHistory(@RequestBody Map<String, Object> request) {
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        if (messages == null || messages.isEmpty()) {
            return Result.error("消息不能为空");
        }
        try {
            String reply = preDiagnosisService.chatWithHistory(messages);
            Result<String> result = Result.success();
            result.setData(reply);
            return result;
        } catch (Exception e) {
            return Result.error("AI 服务暂不可用，请稍后重试");
        }
    }
}
