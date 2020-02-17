package com.tianyou.spring.framework.webmvc.servlet;

import com.tianyou.spring.framework.annotation.TyRequestMapping;
import com.tianyou.spring.framework.annotation.TyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TyHandlerAdapter {

    public boolean supports(Object handler){
        return handler instanceof TyHandlerMapping;
    }

    TyModelAndView handle(HttpServletRequest req, HttpServletResponse resp,Object handler) throws InvocationTargetException, IllegalAccessException {
        TyHandlerMapping handlerMapping=(TyHandlerMapping)handler;
        //记录方法参数名称以及参数位置
        Map<String,Integer> paramIndexMapping=new HashMap<String,Integer>();

        //提取方法中加了注解的参数,记录参数位置并存到paramIndexMapping容器中
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[] [] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof TyRequestParam){
                    String paramName = ((TyRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //获取所有方法参数类型，提取req和resp类型，记录这2个类型的参数位置并存到paramIndexMapping容器中
        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length ; i ++) {
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class ||
                    type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }

        //获得方法的形参列表（request.getParameterMap()返回的是一个Map类型的值，该返回值记录着前端（如jsp页面）所提交请求中的请求参数和请求参数值的映射关系）
        //因为相同参数名称对应的参数值可能会有多个，如前端checkbox组件，所以这里是String,String[]的结构
        Map<String,String[]> params = req.getParameterMap();

        //实参列表
        Object [] paramValues = new Object[paramsTypes.length];

        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");

            if(!paramIndexMapping.containsKey(parm.getKey())){continue;}

            //获取参数所在位置
            int index = paramIndexMapping.get(parm.getKey());

            //将参数值转换成其他类型
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }

        //将req对象加入实参列表
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        //将resp对象加入实参列表
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //方法执行逻辑，把handlermapping中的controller和实参列表作为invoke的参数传入
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(result == null || result instanceof Void){
            return null;
        }

        //判断方法的返回值是不是ModelAndView类型
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == TyModelAndView.class;
        if(isModelAndView){
            return (TyModelAndView) result;
        }

        return null;
    }

    /**
     * 将String类型转换成其他任意类型
     * @param value 原String类型值
     * @param paramsType 要转换成的类型
     * @return
     */
    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){
            return value;
        }
        //如果是int
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }
        else if(Double.class == paramsType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现

    }
}
