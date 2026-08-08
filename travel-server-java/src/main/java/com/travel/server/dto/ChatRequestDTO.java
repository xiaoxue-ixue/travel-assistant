package com.travel.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "消息内容不能为空")
    private String message;
}
