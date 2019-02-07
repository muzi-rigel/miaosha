package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    public UserModel getUserById(int id);

    public void register(UserModel model) throws Exception;

    public UserModel validateLogin(String telephone, String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;
}
