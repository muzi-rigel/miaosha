package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {



    //定义exception handler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Object handlerException(HttpServletRequest request, Exception e) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus("fail");
        e.printStackTrace();
        Map<String, Object> responseData = new HashMap<>();
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            responseData.put("errCode", be.getErrCode());
            responseData.put("errMsg", be.getErrMsg());
        } else {
            responseData.put("errCode", EmBusinessError.SYSTEM_ERROR.getErrCode());
            responseData.put("errMsg", EmBusinessError.SYSTEM_ERROR.getErrMsg());
        }
        type.setData(responseData);
        return type;
    }
}
