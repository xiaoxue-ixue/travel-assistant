package com.travel.server.controller;

import com.travel.server.dto.ChatRequestDTO;
import com.travel.server.dto.TravelRequestDTO;
import com.travel.server.service.TravelService;
import com.travel.server.vo.ChatVO;
import com.travel.server.vo.Result;
import com.travel.server.vo.TravelRecommendVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;
    private final ScheduledExecutorService timeoutScheduler = Executors.newScheduledThreadPool(4);

    @PostMapping("/recommend")
    public Result<TravelRecommendVO> recommend(@Valid @RequestBody TravelRequestDTO request) throws IOException {
        log.info("收到旅游规划请求: city={}, budget={}, days={}", request.getCity(), request.getBudget(), request.getDays());
        TravelRecommendVO vo = travelService.recommend(request);
        return Result.success(vo);
    }

    @PostMapping("/chat")
    public Result<ChatVO> chat(@Valid @RequestBody ChatRequestDTO request) throws IOException {
        log.info("收到聊天请求: message={}", request.getMessage());
        ChatVO vo = travelService.chat(request);
        return Result.success(vo);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@Valid @RequestBody ChatRequestDTO request) {
        log.info("收到流式聊天请求: message={}", request.getMessage());

        SseEmitter emitter = new SseEmitter(180_000L);

        timeoutScheduler.schedule(() -> {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }, 185, TimeUnit.SECONDS);

        emitter.onCompletion(() -> log.debug("SSE连接完成"));
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时");
            emitter.complete();
        });
        emitter.onError(e -> log.error("SSE连接错误", e));

        travelService.streamChat(request, emitter);

        return emitter;
    }
}
