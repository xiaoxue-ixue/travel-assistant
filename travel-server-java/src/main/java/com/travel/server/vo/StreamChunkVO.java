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
public class StreamChunkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String content;
}
