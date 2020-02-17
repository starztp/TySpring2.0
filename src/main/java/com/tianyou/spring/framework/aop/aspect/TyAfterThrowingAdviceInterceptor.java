package com.tianyou.spring.framework.aop.aspect;

import com.tianyou.spring.framework.aop.intercept.TyMethodInterceptor;
import com.tianyou.spring.framework.aop.intercept.TyMethodInvocation;

import java.lang.reflect.Method;

public class TyAfterThrowingAdviceInterceptor extends TyAbstractAspectAdvice implements TyMethodInterceptor,TyAdvice {

    private String throwingName;
    /**
     * @param aspectMethod 执行切面的目标方法
     * @param target
     */
    public TyAfterThrowingAdviceInterceptor(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    @Override
    public Object invoke(TyMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(invocation,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
