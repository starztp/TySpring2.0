package com.tianyou.spring.framework.beans;

/**
 * 单例工厂的顶层设计
 */
public interface TyBeanFactory {

    Object getBean(String beanname);
}
