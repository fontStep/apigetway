package com.jst.apigetway.controller;


import com.alibaba.fastjson.JSON;
import com.jst.apigetway.common.constant.ApiCommConstant;
import com.jst.apigetway.common.core.ApiGetWayHand;
import com.jst.apigetway.common.dto.RequestLog;
import com.jst.apigetway.common.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangjiawei
 *
 * */
@RestController
@Slf4j
public class ApiController {

    @Autowired
    private ApiGetWayHand apiGetWayHand;

    final RequestLog requestLog ;

    public ApiController() {
        requestLog = new RequestLog();
    }

    /**
     * post请求
     * @param request
     * @return
     */
    @PostMapping(value = "api")
    public Object apiPost(HttpServletRequest request){
        return operator(request);
    }


    /**
     * get请求
     * @param request
     * @return
     */
    @GetMapping(value = "api")
    public Object apiGet(HttpServletRequest request){
        return operator(request);
    }




    public Object operator(HttpServletRequest request){
        requestLog.setUrl(request.getRequestURL().toString());
        requestLog.setIp(RequestUtil.getRequestIp(request));
        requestLog.setMethodName(request.getParameter(ApiCommConstant.METHOD));
        requestLog.setParams(request.getParameter(ApiCommConstant.PARAMS));
        requestLog.setStartTime(System.currentTimeMillis());

        Object result =  apiGetWayHand.hand(request);

        requestLog.setEndTime(System.currentTimeMillis());
        requestLog.setResultData(JSON.toJSONString(result));

        log.info("requestLog  日志记录：{}",JSON.toJSONString(requestLog));

        return  result;
    }
}
