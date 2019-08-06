package com.jst.apigetway.common.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getRequestIp(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }
}
