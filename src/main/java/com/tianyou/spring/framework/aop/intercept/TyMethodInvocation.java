package com.tianyou.spring.framework.aop.intercept;

import com.tianyou.spring.framework.aop.aspect.TyJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TyMethodInvocation implements TyJoinPoint {

    private Object proxy;//代理对象
    private Method method;
    private Object target;//目标对象
    private Object [] arguments;//参数
    private List<Object> interceptorsAndDynamicMethodMatchers; //执行器链
    private Class<?> targetClass;//目标类
    //定义一个索引，从-1开始来记录当前拦截器执行的位置
    private int currentInterceptorIndex = -1;



    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public List<Object> getInterceptorsAndDynamicMethodMatchers() {
        return interceptorsAndDynamicMethodMatchers;
    }

    public void setInterceptorsAndDynamicMethodMatchers(List<Object> interceptorsAndDynamicMethodMatchers) {
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public int getCurrentInterceptorIndex() {
        return currentInterceptorIndex;
    }

    public void setCurrentInterceptorIndex(int currentInterceptorIndex) {
        this.currentInterceptorIndex = currentInterceptorIndex;
    }

    public TyMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        //如果Interceptor执行完了，则执行joinPoint
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target,this.arguments);
        }

        //如果执行器链没有执行完，则获取执行器链中的通知
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        //如果要动态匹配joinPoint
        if (interceptorOrInterceptionAdvice instanceof TyMethodInterceptor) {
            TyMethodInterceptor mi = (TyMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);//this指的是MethodInvocation本身
        } else {
            //动态匹配失败时,略过当前Intercetpor,调用下一个Interceptor
            return proceed();
        }
    }

    @Override
    public Object getthis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
