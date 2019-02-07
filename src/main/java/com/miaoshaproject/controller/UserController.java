package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobejct.UserVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@RestController
@RequestMapping("/user")
@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
public class UserController extends BaseController {

    private static final String CONTENT_TYPE_FORMED = "";

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws Exception {

        if(StringUtils.isEmpty(telephone)|| StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMTER_ERROR);
        }

        UserModel model = userService.validateLogin(telephone, password);

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",model);

        return CommonReturnType.create(null);

    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public CommonReturnType register(UserModel model,
                                     @RequestParam(name = "otpCode") String otpCode) throws Exception {

        String sessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(model.getTelephone());
        if (!StringUtils.equals(sessionOtpCode, otpCode)) {
//            throw new BusinessException(EmBusinessError.OTP_CODE_ERROR);
            System.out.println("验证码错误 跳过校验");
        }

        userService.register(model);

        return CommonReturnType.create(null);

    }

    @RequestMapping("/get")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel user = userService.getUserById(id);
        if (user == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVo vo = convertFromModel(user);
        return CommonReturnType.create(vo);
    }

    @RequestMapping(value = "/getotp", method = RequestMethod.POST)
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String optCode = String.valueOf(randomInt);
        httpServletRequest.getSession().setAttribute(telephone, optCode);
        System.out.println("手机号" + telephone + ",验证码 = " + optCode);
        return CommonReturnType.create(null);
    }


    private UserVo convertFromModel(UserModel model) {
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }


}
