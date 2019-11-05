package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPassword;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.util.RandomValue;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(int id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        UserPassword password = userPasswordMapper.selectByUserId(id);
        return convertFromDateObject(userDO, password);
    }

    @Override
//    @Transactional
    public void register(UserModel userModel) throws Exception {

        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMTER_ERROR);
        }

//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getAge() == null
//                || userModel.getGender() == null
//                || StringUtils.isEmpty(userModel.getTelephone())) {
//            throw new BusinessException(EmBusinessError.PARAMTER_ERROR);
//        }


        ValidationResult validate = validator.validate(userModel);
        if (validate.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMTER_ERROR, validate.getErrMsg());
        }

        UserDO user = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(EmBusinessError.TELEPHONE_DUPLICATE_ERROR);
        }

        userModel.setId(user.getId());

        UserPassword userPassword = convertPasswordFromModel(userModel);
        userPasswordMapper.insertSelective(userPassword);

    }

    @Override
    public void addBatch(List<UserModel> list) {
        List<UserDO> userDOS = new ArrayList<>();
        for (UserModel userModel : list) {
            UserDO userDo = convertFromModel(userModel);
            userDOS.add(userDo);
        }
        userDOMapper.insertBatch(userDOS);
    }

    @Override
    public UserModel validateLogin(String telephone, String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        if (null == userDO) {
            throw new BusinessException(EmBusinessError.LOGIN_FAIL);
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDateObject(userDO, userPassword);

        if (!userPassword.getEncrptPassword().equals(getMd5(password))) {
            throw new BusinessException(EmBusinessError.LOGIN_FAIL);
        }
        return userModel;
    }

    @Override
    public void batchRegister() {
        long start = System.currentTimeMillis();
        List<UserModel> modelList = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        int num = 1000000;

        for (int i = 0; i < num; i++) {
            if (i % 1000 == 0) {
                service.submit(new ThreadDemo(modelList, this));
                modelList = new ArrayList<>();
            }
            modelList.add(RandomValue.getRandomUser());
        }
        System.out.println("耗时" + (System.currentTimeMillis() - start));

    }

    class ThreadDemo extends Thread {
        private List<UserModel> userModelList;
        private UserServiceImpl userService;

        ThreadDemo(List<UserModel> userModelList, UserServiceImpl userService) {
            this.userModelList = userModelList;
            this.userService = userService;
        }

        public void run() {
            try {
                userService.addBatch(userModelList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private UserPassword convertPasswordFromModel(UserModel userModel) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (userModel == null) {
            return null;
        }
        UserPassword password = new UserPassword();
        password.setUserId(userModel.getId());
        if (null != userModel.getEncriptPassword()) {
            password.setEncrptPassword(getMd5(userModel.getEncriptPassword()));
        }
        return password;
    }

    private String getMd5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encode = base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
        return encode;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO user = new UserDO();
        BeanUtils.copyProperties(userModel, user);
        return user;
    }

    private UserModel convertFromDateObject(UserDO userDo, UserPassword userPassword) {
        if (userDo == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDo, userModel);
        if (userPassword != null) {
            userModel.setEncriptPassword(userPassword.getEncrptPassword());
        }
        return userModel;
    }
}
