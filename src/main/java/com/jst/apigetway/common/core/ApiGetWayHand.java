package com.jst.apigetway.common.core;


import com.alibaba.fastjson.JSON;
import com.jst.apigetway.common.constant.ApiCommConstant;
import com.jst.apigetway.common.dto.HandResult;
import com.jst.apigetway.common.dto.RequestLog;
import com.jst.apigetway.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *  api 处理类
 *  @author wangjiawei
 */
@Component
@Slf4j
public class ApiGetWayHand implements InitializingBean , ApplicationContextAware {


    private ApiStore apiStore;

    final ParameterNameDiscoverer parameterUtil;



    public ApiGetWayHand() {
        parameterUtil = new LocalVariableTableParameterNameDiscoverer();

    }


    /**
     * 属性初始化后执行该方法 初始化API 工厂
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        apiStore.loadApiFromSpringBeans();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        apiStore = new ApiStore(applicationContext);
    }

    /**
     * 处理 API请求  默认情况下返回 ""
     *
     * @param request
     * @return
     */
    public Object hand(HttpServletRequest request) {


        String method = request.getParameter(ApiCommConstant.METHOD);

        String params = request.getParameter(ApiCommConstant.PARAMS);



        /**
         * 1. 校验方法名 和参数
         */
        try {
          ApiRunnable apiRunnable =   paramsValdate(method,params);

         Object[] args =  buildParams(apiRunnable,method,params);

         if(args !=null){
             /**
              * 对象事例 参数
              */



             Object object =  apiRunnable.getTargetMethod().invoke(apiRunnable.getBeanInstance(),args);


             HandResult handResult = new HandResult();

             handResult.setType("notify");

             List data = new ArrayList(2);
             data.add(object);
             handResult.setData(data);

             return  handResult;
         }
        } catch (BusinessException e) {
            log.error("校验参数出现错误:   error:{}",e);
            return handErrorResult(e.getMessage());
        } catch (Exception e) {
            log.error("出现错误:   error:{}",e);
            return handErrorResult(e.getMessage());
        }

        return "";
    }


    /**
     * 处理错误结果封装
     * @param message
     * @return
     */
    private HandResult handErrorResult(String message){
        HandResult handResult = new HandResult();
        handResult.setType("fail");
        handResult.setData(new ArrayList<>(2));
        return  handResult;
    }

    /**
     * 将params参数转化为 method中的参数
     * @param apiRunnable
     * @param method
     * @param params
     */
    private Object[] buildParams(ApiRunnable apiRunnable, String method, String params) throws Exception {
        Map<String,Object> paramMap = JSON.parseObject(params);

        /**
         * 根据method名 获取 所需要的参数
         */

        Method targetMethod = apiRunnable.getTargetMethod();



        /**
         * 获取所有的参数名称
         */
        List<String> paramNames = Arrays.asList(parameterUtil.getParameterNames(targetMethod));

        for (Map.Entry<String, Object> m:paramMap.entrySet()){


            if(!paramNames.contains(m.getKey())){
                throw new BusinessException( "调用失败：接口不存在‘" + m.getKey() + "’参数");
            }


        }

        /**
         * 校验参数类型
         */
        Class<?>[] parameterTypes =  targetMethod.getParameterTypes();


        if(null != parameterTypes && parameterTypes.length>0){
            Object[] args = new Object[parameterTypes.length];
            /**
             * 获取调用者传输的参数
             */
            for (int i=0;i<parameterTypes.length;i++){
                if(paramMap.containsKey(paramNames.get(i))){

                    //** 转换成对应的类型
                    args[i] = convertJsonToBean(paramMap.get(paramNames.get(i)), parameterTypes[i]);
                }
            }

            return args;

        }

        return null;



    }


    private <T> Object convertJsonToBean(Object val, Class<T> targetClass) throws Exception {
        Object result = null;
        if (val == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            result = Integer.parseInt(val.toString());
        } else if (Long.class.equals(targetClass)) {
            result = Long.parseLong(val.toString());
        } else if (Date.class.equals(targetClass)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                result = sdf.parse(val.toString());
            }catch (Exception e){
              throw new Exception("时间格式类型必须是 yyyy-MM-dd HH:mm:ss");
            }
        } else if (String.class.equals(targetClass)) {
            if (val instanceof String) {
                result = val.toString();
            } else {
                throw new IllegalArgumentException("转换目标类型为字符串");
            }
        } else {
            return JSON.parseObject(val.toString(),targetClass);

        }
        return result;
    }


    /**
     * 校验参数是否合法
     * @param apiName
     * @param params
     * @return
     * @throws BusinessException
     */
    private ApiRunnable paramsValdate(String apiName, String params) throws BusinessException {

        if(null == apiName || StringUtils.isEmpty(apiName.trim())){
            log.error("method参数为空");
            throw new BusinessException("method参数为空");
        }else if(null == params){
            log.error("params参数为空");
            throw new BusinessException("params参数为空");
        }else if(null == apiStore.findApiRunnable(apiName)){
            throw new BusinessException( "调用失败：指定API不存在，API:" + apiName);
        }

        return  apiStore.findApiRunnable(apiName);
    }


}
