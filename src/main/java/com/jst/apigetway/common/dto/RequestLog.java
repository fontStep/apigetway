package com.jst.apigetway.common.dto;

import lombok.Data;

@Data
public class RequestLog {

    /**
     * 请求的serverName 默认 ""
     */
    private String methodName = "";


    /**
     * 客户端IP地址
     */
    private String ip;


    /**
     * 请求的地址
     */
    private String url;


    /**
     * 请求所携带的方法参数
     */
    private String params;


    /**
     * 开始时间
     */
    private Long startTime;


    /**
     * 结束时间
     */
    private Long endTime;


    /**
     * 返回结果
     */
    private String resultData;

}
