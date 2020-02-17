package com.tianyou.spring.framework.aop.aspect;

import com.tianyou.spring.framework.aop.intercept.TyMethodInterceptor;
import com.tianyou.spring.framework.aop.intercept.TyMethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TyAfterReturningAdviceInterceptor extends TyAbstractAspectAdvice implements TyMethodInterceptor,TyAdvice {

    private TyJoinPoint joinPoint;
    /**
     * @param aspectMethod 执行切面的目标方法
     * @param target
     */
    public TyAfterReturningAdviceInterceptor(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    @Override
    public Object invoke(TyMethodInvocation invocation) throws Throwable{
        Object returnvalue=invocation.proceed();
        this.joinPoint=invocation;
        afterReturning(returnvalue,invocation.getMethod(),invocation.getArguments(),invocation.getthis());
        return returnvalue;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
