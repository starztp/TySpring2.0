package com.tianyou.spring.framework.aop.aspect;

import java.lang.reflect.Method;

public interface TyJoinPoint {

    //获取切面本身对象
    Object getthis();

    //获取参数列表
    Object[] getArguments();

    //获取方法
    Method getMethod();

}
