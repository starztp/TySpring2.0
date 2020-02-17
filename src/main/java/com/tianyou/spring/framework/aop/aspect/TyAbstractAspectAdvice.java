package com.tianyou.spring.framework.aop.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class TyAbstractAspectAdvice implements TyAdvice{

    private Method aspectMethod;
    private Object target;

    /**
     *
     * @param aspectMethod 执行切面的目标方法
     * @param target
     */
    public TyAbstractAspectAdvice(Method aspectMethod, Object target){
        this.aspectMethod=aspectMethod;
        this.target=target;
    }

    //执行切面方法本身
    public Object invokeAdviceMethod(TyJoinPoint joinPoint,Object returnValue,Throwable tx) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] paramtypes=this.aspectMethod.getParameterTypes();
        //实参列表
        Object[]args=new Object[paramtypes.length];
        //如果方法没有参数就直接执行方法
        if(paramtypes.length==0||paramtypes==null){
            return this.aspectMethod.invoke(target);
        }else {

            for(int i=0;i<paramtypes.length;i++){
                if(paramtypes[i]==TyJoinPoint.class){
                    args[i]=TyJoinPoint.class;
                }else if(paramtypes[i]==Throwable.class){
                    args[i]=Throwable.class;
                }else if(paramtypes[i]==Object.class){
                    args[i]=returnValue;
                }
            }
        }
        return this.aspectMethod.invoke(target,args);

    }
}
