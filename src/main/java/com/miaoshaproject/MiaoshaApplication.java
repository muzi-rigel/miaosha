package com.miaoshaproject;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@RestController
@MapperScan("com.miaoshaproject.dao")
public class MiaoshaApplication {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String hello(){
        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        if(userDO != null){
            return userDO.getName();
        }else{
            return "用户不存在！";
        }
    }

    @RequestMapping("/test1")
    public String test1(){
        userService.batchRegister();
        return "success";
    }


    public static void main(String[] args) {
        SpringApplication.run(MiaoshaApplication.class, args);
    }

}

