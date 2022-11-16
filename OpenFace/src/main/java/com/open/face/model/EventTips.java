package com.open.face.model;

/**
 * Created by Jowney on 2018/7/9.
 */

public class EventTips<T> {
    private T data;
    private int code;

    public EventTips(T data, int code) {
        this.data = data;
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
