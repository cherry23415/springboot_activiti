package com.ying.dto.resp;

import com.ying.constant.BaseResultEnum;

import java.io.Serializable;

/**
 * 返回值对象
 * Created by lyz on 2017/6/13.
 */
public class BaseRespDto<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    public BaseRespDto() {
    }

    public BaseRespDto(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BaseRespDto(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public BaseRespDto(BaseResultEnum baseResultEnum) {
        this.status = baseResultEnum.getStatus();
        this.msg = baseResultEnum.getMsg();
    }

    public BaseRespDto(BaseResultEnum baseResultEnum, T data) {
        this.status = baseResultEnum.getStatus();
        this.msg = baseResultEnum.getMsg();
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
