package com.miaoshaproject.error;

public enum EmBusinessError implements CommonError {
    SYSTEM_ERROR(10000, "系统异常"),
    USER_NOT_EXIST(10001, "用户不存在"),
    OTP_CODE_ERROR(10002, "验证码错误"),
    PARAMTER_ERROR(10003, "参数错误"),
    TELEPHONE_DUPLICATE_ERROR(10004, "手机号已重复注册"),

    LOGIN_FAIL(20001, "用户手机号或密码错误"),
    ;


    private int errCode;
    private String errMsg;

    EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
