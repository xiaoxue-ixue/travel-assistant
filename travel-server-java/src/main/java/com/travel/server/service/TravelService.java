package com.travel.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.server.dto.ChatRequestDTO;
import com.travel.server.dto.TravelRequestDTO;
import com.travel.server.utils.LLMUtils;
import com.travel.server.vo.ChatVO;
import com.travel.server.vo.TravelRecommendVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelService {

    private final LLMUtils llmUtils;
    private final ObjectMapper objectMapper;

    private static final String TRAVEL_SYSTEM_PROMPT = "你是一个专业的旅游规划师，请根据用户提供的城市、预算和天数，生成一份详细的旅游规划。" +
            "请严格按照以下JSON格式返回，不要输出任何额外的文字说明，只返回JSON：\n" +
            "{\n" +
            "  \"success\": true,\n" +
            "  \"city\": \"城市名\",\n" +
            "  \"days\": 天数,\n" +
            "  \"totalBudget\": 总预算金额,\n" +
            "  \"dailyItinerary\": [\n" +
            "    {\n" +
            "      \"day\": 1,\n" +
            "      \"date\": \"第1天\",\n" +
            "      \"morning\": {\"spot\": \"景点名称\", \"duration\": \"游览时长\", \"ticket\": \"门票价格\", \"transportation\": \"交通方式\", \"description\": \"景点介绍\"},\n" +
            "      \"afternoon\": {\"spot\": \"景点名称\", \"duration\": \"游览时长\", \"ticket\": \"门票价格\", \"transportation\": \"交通方式\", \"description\": \"景点介绍\"},\n" +
            "      \"evening\": {\"spot\": \"活动名称\", \"duration\": \"活动时长\", \"ticket\": \"费用\", \"transportation\": \"交通方式\", \"description\": \"活动介绍\"}\n" +
            "    }\n" +
            "  ],\n" +
            "  \"budgetBreakdown\": {\"accommodation\": 1500, \"food\": 1200, \"transportation\": 800, \"tickets\": 1000, \"other\": 500},\n" +
            "  \"tips\": [\"提示1\", \"提示2\", \"提示3\"],\n" +
            "  \"warnings\": [\"注意事项1\", \"注意事项2\"]\n" +
            "}";

    private static final String CHAT_SYSTEM_PROMPT = "你是一个友好的旅游助手,请用中文回答用户关于旅游的问题";

    public TravelRecommendVO recommend(TravelRequestDTO request) throws IOException {
        log.info("开始生成旅游规划: 城市={}, 预算={}, 天数={}", request.getCity(), request.getBudget(), request.getDays());

        String userMessage = String.format("请为我规划%s的%d日游，总预算为%d元。请提供详细的每日行程安排（上午、下午、晚上），包括景点名称、游览时长、门票价格、交通方式和景点介绍。同时请给出预算明细分类（住宿、餐饮、交通、门票、其他）以及3-5条温馨提示和注意事项。预算分配要合理，总花费不能超过预算。注意事项要贴合该城市的实际情况。",
                request.getCity(), request.getDays(), request.getBudget());

        try {
            String llmResponse = llmUtils.chatCompletion(TRAVEL_SYSTEM_PROMPT, userMessage);
            log.debug("LLM原始响应: {}", llmResponse);

            String jsonStr = llmUtils.extractJsonFromText(llmResponse);
            TravelRecommendVO vo = objectMapper.readValue(jsonStr, TravelRecommendVO.class);

            if (vo.getSuccess() == null) {
                vo.setSuccess(true);
            }
            if (vo.getCity() == null) {
                vo.setCity(request.getCity());
            }
            if (vo.getDays() == null) {
                vo.setDays(request.getDays());
            }
            if (vo.getTotalBudget() == null) {
                vo.setTotalBudget(request.getBudget());
            }

            log.info("旅游规划生成成功: 城市={}, 天数={}", vo.getCity(), vo.getDays());
            return vo;
        } catch (IOException e) {
            log.error("旅游规划生成失败", e);
            throw new IOException("生成旅游规划失败: " + e.getMessage(), e);
        }
    }

    public ChatVO chat(ChatRequestDTO request) throws IOException {
        log.info("开始聊天问答: 消息={}", request.getMessage());

        try {
            String response = llmUtils.chatCompletion(CHAT_SYSTEM_PROMPT, request.getMessage());
            return ChatVO.builder()
                    .success(true)
                    .content(response)
                    .role("assistant")
                    .build();
        } catch (IOException e) {
            log.error("聊天问答失败", e);
            throw new IOException("AI回答生成失败: " + e.getMessage(), e);
        }
    }

    public void streamChat(ChatRequestDTO request, SseEmitter emitter) {
        log.info("开始流式聊天问答: 消息={}", request.getMessage());

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", CHAT_SYSTEM_PROMPT);
        messages.add(sysMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", request.getMessage());
        messages.add(userMsg);

        llmUtils.streamChatCompletion(messages, emitter);
    }
}
