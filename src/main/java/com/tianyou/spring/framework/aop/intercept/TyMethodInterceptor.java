package com.tianyou.spring.framework.aop.intercept;

import java.lang.reflect.InvocationTargetException;

public interface TyMethodInterceptor {

    Object invoke(TyMethodInvocation invocation) throws Throwable;
}
