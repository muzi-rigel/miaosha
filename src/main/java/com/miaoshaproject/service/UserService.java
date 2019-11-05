package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {

    public UserModel getUserById(int id);

    public void register(UserModel model) throws Exception;

    public void addBatch(List<UserModel> list);

    public UserModel validateLogin(String telephone, String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;

    public void batchRegister();
}
