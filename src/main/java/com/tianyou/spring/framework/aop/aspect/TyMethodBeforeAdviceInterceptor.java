package com.tianyou.spring.framework.aop.aspect;

import com.tianyou.spring.framework.aop.intercept.TyMethodInterceptor;
import com.tianyou.spring.framework.aop.intercept.TyMethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TyMethodBeforeAdviceInterceptor extends TyAbstractAspectAdvice implements TyMethodInterceptor,TyAdvice {

    private TyJoinPoint joinPoint;
    /**
     * @param aspectMethod 执行切面的目标方法
     * @param target
     */
    public TyMethodBeforeAdviceInterceptor(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

    @Override
    public Object invoke(TyMethodInvocation invocation) throws Throwable {
        joinPoint=invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getthis());
        return invocation.proceed();
    }
}
