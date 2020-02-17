package com.tianyou.spring.framework.beans.support;

import com.tianyou.spring.framework.beans.TyBeanFactory;
import com.tianyou.spring.framework.beans.config.TyBeanDefinition;
import com.tianyou.spring.framework.context.support.TyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC容器初始化在不扩展的情况下的默认实现
 */
public class TyDefaultListableBeanFactory extends TyAbstractApplicationContext implements TyBeanFactory {

    //存储BeanDefinition的容器
    protected final Map<String,TyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,TyBeanDefinition>();

    @Override
    public Object getBean(String beanname) throws Exception {
        return null;
    }

    @Override
    public Object getBean(Class<?> beanclass) throws Exception {
        return null;
    }

}
