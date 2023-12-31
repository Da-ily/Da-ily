package com.daily.daily.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommonResponseDTO {
    private boolean isSuccessful;
    private Integer statusCode;
}
