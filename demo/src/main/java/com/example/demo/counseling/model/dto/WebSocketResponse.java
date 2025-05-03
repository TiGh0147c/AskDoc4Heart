package com.example.demo.counseling.model.dto;

import lombok.Data;

@Data
public class WebSocketResponse {
    private String status;
    private String message;
    private Object data;

    public static WebSocketResponse success(Object data) {
        WebSocketResponse response = new WebSocketResponse();
        response.setStatus("success");
        response.setData(data);
        return response;
    }

    public static WebSocketResponse error(String message) {
        WebSocketResponse response = new WebSocketResponse();
        response.setStatus("error");
        response.setMessage(message);
        return response;
    }
}