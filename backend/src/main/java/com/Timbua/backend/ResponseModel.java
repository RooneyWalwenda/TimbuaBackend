package com.Timbua.backend;

public class ResponseModel<T> {
    private T data;
    private String messageCode;
    private String message;

    public ResponseModel() {}

    public ResponseModel(T data, String messageCode, String message) {
        this.data = data;
        this.messageCode = messageCode;
        this.message = message;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getMessageCode() { return messageCode; }
    public void setMessageCode(String messageCode) { this.messageCode = messageCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
