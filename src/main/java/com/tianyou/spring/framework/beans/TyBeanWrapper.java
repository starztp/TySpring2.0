package com.tianyou.spring.framework.beans;

/**
 * Created by tianyou on 2020/1/22.
 */
public class TyBeanWrapper {

    private Object wrappedInstance;//实例化的类
    private Class<?> wrappedClass;  //实例化类的class

    public TyBeanWrapper(Object wrappedInstance){
        this.wrappedInstance=wrappedInstance;
    }


    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
