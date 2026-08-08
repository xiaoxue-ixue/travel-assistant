package com.travel.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LLMUtils {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${llm.provider:siliconflow}")
    private String provider;

    @Value("${llm.siliconflow.api-key:}")
    private String siliconflowApiKey;

    @Value("${llm.siliconflow.base-url:https://api.siliconflow.cn/v1}")
    private String siliconflowBaseUrl;

    @Value("${llm.siliconflow.model:deepseek-ai/DeepSeek-V3}")
    private String siliconflowModel;

    @Value("${llm.siliconflow.chat-model:deepseek-ai/DeepSeek-V3}")
    private String siliconflowChatModel;

    @Value("${llm.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${llm.deepseek.base-url:https://api.deepseek.com/v1}")
    private String deepseekBaseUrl;

    @Value("${llm.deepseek.model:deepseek-chat}")
    private String deepseekModel;

    @Value("${llm.deepseek.chat-model:deepseek-chat}")
    private String deepseekChatModel;

    @Value("${llm.timeout:180000}")
    private long timeout;

    public LLMUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private String getApiKey() {
        return "siliconflow".equalsIgnoreCase(provider) ? siliconflowApiKey : deepseekApiKey;
    }

    private String getBaseUrl() {
        return "siliconflow".equalsIgnoreCase(provider) ? siliconflowBaseUrl : deepseekBaseUrl;
    }

    private String getModel() {
        return "siliconflow".equalsIgnoreCase(provider) ? siliconflowModel : deepseekModel;
    }

    private String getChatModel() {
        return "siliconflow".equalsIgnoreCase(provider) ? siliconflowChatModel : deepseekChatModel;
    }

    public String chatCompletion(String systemPrompt, String userMessage) throws IOException {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);
        }
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return chatCompletion(messages, false);
    }

    public String chatCompletion(List<Map<String, String>> messages, boolean stream) throws IOException {
        String url = getBaseUrl() + "/chat/completions";
        String apiKey = getApiKey();
        String model = stream ? getChatModel() : getModel();

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("stream", stream);
        body.put("temperature", 0.7);

        String jsonBody = objectMapper.writeValueAsString(body);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                log.error("LLM API call failed: {} - {}", response.code(), errorBody);
                throw new IOException("LLM API调用失败: HTTP " + response.code() + " - " + errorBody);
            }

            if (response.body() == null) {
                throw new IOException("LLM API响应体为空");
            }

            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode messageNode = firstChoice.get("message");
                if (messageNode != null && messageNode.get("content") != null) {
                    return messageNode.get("content").asText();
                }
            }
            log.error("无法解析LLM响应: {}", responseBody);
            throw new IOException("无法解析LLM响应内容");
        }
    }

    public void streamChatCompletion(String systemPrompt, String userMessage, SseEmitter emitter) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);
        }
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        streamChatCompletion(messages, emitter);
    }

    public void streamChatCompletion(List<Map<String, String>> messages, SseEmitter emitter) {
        new Thread(() -> {
            try {
                String url = getBaseUrl() + "/chat/completions";
                String apiKey = getApiKey();
                String model = getChatModel();

                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("messages", messages);
                body.put("stream", true);
                body.put("temperature", 0.7);

                String jsonBody = objectMapper.writeValueAsString(body);

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "text/event-stream")
                        .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        log.error("Stream chat failed: {} - {}", response.code(), errorBody);
                        Map<String, String> errorEvent = new HashMap<>();
                        errorEvent.put("type", "error");
                        errorEvent.put("error", "API调用失败: " + errorBody);
                        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorEvent)));
                        emitter.complete();
                        return;
                    }

                    if (response.body() == null) {
                        Map<String, String> errorEvent = new HashMap<>();
                        errorEvent.put("type", "error");
                        errorEvent.put("error", "响应体为空");
                        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorEvent)));
                        emitter.complete();
                        return;
                    }

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data:")) {
                                String data = line.substring(5).trim();
                                if ("[DONE]".equals(data)) {
                                    break;
                                }
                                if (!data.isEmpty()) {
                                    try {
                                        JsonNode root = objectMapper.readTree(data);
                                        JsonNode choices = root.get("choices");
                                        if (choices != null && choices.isArray() && choices.size() > 0) {
                                            JsonNode delta = choices.get(0).get("delta");
                                            if (delta != null && delta.get("content") != null && !delta.get("content").isNull()) {
                                                String content = delta.get("content").asText();
                                                if (!content.isEmpty()) {
                                                    Map<String, String> chunkEvent = new HashMap<>();
                                                    chunkEvent.put("type", "chunk");
                                                    chunkEvent.put("content", content);
                                                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(chunkEvent)));
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.debug("解析流式数据行失败: {}", data, e);
                                    }
                                }
                            }
                        }
                    }

                    Map<String, String> doneEvent = new HashMap<>();
                    doneEvent.put("type", "done");
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(doneEvent)));
                    emitter.complete();
                }
            } catch (Exception e) {
                log.error("流式聊天异常", e);
                try {
                    Map<String, String> errorEvent = new HashMap<>();
                    errorEvent.put("type", "error");
                    errorEvent.put("error", e.getMessage());
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorEvent)));
                } catch (IOException ignored) {
                }
                emitter.completeWithError(e);
            }
        }, "llm-stream-thread").start();
    }

    public String extractJsonFromText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return text.substring(firstBrace, lastBrace + 1);
        }
        return text.trim();
    }
}
