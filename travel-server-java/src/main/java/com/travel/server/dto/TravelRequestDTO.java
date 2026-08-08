package com.travel.server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "目的地城市不能为空")
    private String city;

    @NotNull(message = "预算不能为空")
    @Min(value = 100, message = "预算不能低于100元")
    private Integer budget;

    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数最少为1天")
    @Max(value = 30, message = "天数最多为30天")
    private Integer days;
}
