package com.hospital.appointment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API 配置
 */
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {

    private String apiKey;
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    private String model = "deepseek-chat";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
