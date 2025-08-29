package com.example.project.utils;

import com.example.project.config.DeepSeekProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AISummaryUtil {

    private final DeepSeekProperties config;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String summarize(String content) {
        try {
            // 1. 构造请求体，确保 messages 为 JSON 数组
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", config.getModel());

            ArrayNode messages = objectMapper.createArrayNode();

            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个笔记助手，请帮用户总结笔记内容，输出简明扼要的总结。");

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", content);

            messages.add(systemMsg);
            messages.add(userMsg);

            body.set("messages", messages);

            // 2. 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());

            HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

            // 3. 调用 DeepSeek API
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getBaseUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 4. 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return choices.get(0).path("message").path("content").asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "总结失败，请稍后再试。";
    }
}

