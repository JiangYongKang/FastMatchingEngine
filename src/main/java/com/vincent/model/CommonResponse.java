package com.vincent.model;

import lombok.Data;

@Data
public class CommonResponse {

    private Integer code;
    private String message;
    private Object data;

    public CommonResponse() {
    }

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
