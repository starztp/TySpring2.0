package com.tianyou.spring.framework.aop;

import com.tianyou.spring.framework.aop.intercept.TyMethodInvocation;
import com.tianyou.spring.framework.aop.support.TyAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class TyJdkDynamicAopProxy implements TyAopProxy,InvocationHandler {

    private TyAdvisedSupport advice;

    public TyJdkDynamicAopProxy(TyAdvisedSupport config){
        this.advice =config;
    }

    @Override
    public Object getproxy() {
        return getproxy(this.advice.getTargetclass().getClassLoader());
    }

    @Override
    public Object getproxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advice.getTargetclass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advice.getInterceptorsAndDynamicInterceptionAdvice(method,this.advice.getTargetClass());
        TyMethodInvocation methodInvocation=new TyMethodInvocation(proxy,null,method,args,this.advice.getTargetclass(),interceptorsAndDynamicMethodMatchers);
        methodInvocation.proceed();
        return null;
    }
}
