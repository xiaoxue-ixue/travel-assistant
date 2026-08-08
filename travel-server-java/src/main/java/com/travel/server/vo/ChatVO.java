package com.travel.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String content;
    private String role;
}
