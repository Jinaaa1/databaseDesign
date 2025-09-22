package com.ank.common;

/**
 * Result类用于封装统一的返回格式
 *
 *
 *
 *
 * @author ANK.
 * */
public class Result {
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    /**
     * 状态
     */
    private int state;
    /**
     * 错误消息
     */
    private String message;
    /**
     * 返回正确时候的数据
     */
    private Object data;

    public Result() {
    }

    public Result(String error) {
        state = ERROR;
        this.message = error;
    }

    public Result(Object data) {
        this.state = SUCCESS;
        this.data = data;
    }

    public Result(Throwable e) {
        state = ERROR;
        message = e.getMessage();
    }

    public Result(int state, Throwable e) {
        this.state = state;
        this.message = e.getMessage();
    }

    public static Result success() {
        Result result = new Result();
        result.state = SUCCESS;
        result.message = "成功";
        return result;
    }

    public int getState() { return state;}

    public void setState(int state) { this.state = state; }

    public Object getData() { return data;}

    public void setData(Object data) { this.data = data; }

    public String getMessage() { return message;}

    public void setMessage(String message) { this.message = message;}

    @Override
    public String toString() { return "Result [state=" + state + ", message=" + message + ", data=" + data + "]";}
}
