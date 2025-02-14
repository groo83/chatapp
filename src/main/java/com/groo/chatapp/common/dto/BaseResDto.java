package com.groo.chatapp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class BaseResDto {

    private Integer code;   // 응답 코드
    private String message; // 결과 메세지

    protected BaseResDto() {
        this.code = 200;
        this.message = "Success";
    }

    public BaseResDto(String message) {
        this.code = 200;
        this.message = message;
    }

    public static BaseResDto ok() {
        return new BaseResDto();
    }
}