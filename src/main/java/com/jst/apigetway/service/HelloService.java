package com.jst.apigetway.service;


import com.jst.apigetway.common.annotation.ApiController;
import com.jst.apigetway.common.annotation.ApiRequestMapping;
import com.jst.apigetway.model.UserInfo;
import org.springframework.stereotype.Service;


/**
 * @author wangjiawei
 */
@Service
@ApiController()
public class HelloService {


    @ApiRequestMapping(value = "hello")
    public String hello(String name){
        return name;
    }


    @ApiRequestMapping(value = "helloUser")
    public UserInfo helloUser(UserInfo userInfo){
        return userInfo;
    }
}
