package com.vincent.model;

import lombok.Data;

@Data
public class CommonResponse {

    private Integer code;
    private String message;
    private Object data;

    public CommonResponse() {
        this(200, "success");
    }

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
