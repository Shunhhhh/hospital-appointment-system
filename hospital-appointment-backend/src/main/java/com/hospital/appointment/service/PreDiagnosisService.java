package com.hospital.appointment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hospital.appointment.config.DeepSeekConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预问诊服务 — 调用 DeepSeek API
 */
@Service
public class PreDiagnosisService {

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = "你是一个专业的医疗预问诊助手。请根据患者描述的症状，从以下四个方面回答：\n"
            + "1. 可能的疾病方向\n"
            + "2. 建议就诊科室\n"
            + "3. 就诊前需要注意什么\n"
            + "4. 需要带什么检查报告（如有）\n\n"
            + "注意：请明确告知用户这仅供参考，不能替代医生诊断。回答要简洁清晰，不要使用markdown格式，直接用文字分段描述。";

    /**
     * 单轮对话
     */
    public String chat(String symptom) {
        List<Map<String, String>> messages = new java.util.ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", "患者描述了以下症状：" + symptom);
        messages.add(userMsg);
        return callDeepSeek(messages);
    }

    /**
     * 多轮对话（携带历史消息）
     */
    @SuppressWarnings("unchecked")
    public String chatWithHistory(List<Map<String, String>> messages) {
        return callDeepSeek(messages);
    }

    /**
     * 调用 DeepSeek API
     */
    private String callDeepSeek(List<Map<String, String>> messages) {
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.put("max_tokens", 1024);
            requestBody.put("temperature", 0.7);

            ArrayNode messagesArray = objectMapper.createArrayNode();
            // 在最前面插入 system prompt
            ObjectNode systemNode = objectMapper.createObjectNode();
            systemNode.put("role", "system");
            systemNode.put("content", SYSTEM_PROMPT);
            messagesArray.add(systemNode);

            for (Map<String, String> msg : messages) {
                ObjectNode msgNode = objectMapper.createObjectNode();
                msgNode.put("role", msg.get("role"));
                msgNode.put("content", msg.get("content"));
                messagesArray.add(msgNode);
            }
            requestBody.set("messages", messagesArray);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepSeekConfig.getApiKey());

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    deepSeekConfig.getApiUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 解析响应
            String rawBody = response.getBody();
            if (rawBody == null || rawBody.trim().isEmpty()) {
                throw new RuntimeException("AI 服务返回为空");
            }
            JsonNode root = objectMapper.readTree(rawBody);

            // 检查是否有错误信息
            if (root.has("error")) {
                String errMsg = root.get("error").get("message").asText("未知错误");
                throw new RuntimeException("AI 服务错误: " + errMsg);
            }

            // 检查 choices 是否存在并有内容
            JsonNode choices = root.get("choices");
            if (choices == null || choices.size() == 0) {
                throw new RuntimeException("AI 服务响应异常：未获取到回答");
            }

            String content = choices.get(0).get("message").get("content").asText("");
            if (content.trim().isEmpty()) {
                throw new RuntimeException("AI 服务返回内容为空");
            }
            return content;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("预问诊服务异常: " + e.getMessage());
        }
    }
}
