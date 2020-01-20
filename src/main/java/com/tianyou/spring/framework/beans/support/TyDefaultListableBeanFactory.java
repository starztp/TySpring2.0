package com.tianyou.spring.framework.beans.support;

import com.tianyou.spring.framework.beans.TyBeanFactory;
import com.tianyou.spring.framework.beans.config.TyBeanDefinition;
import com.tianyou.spring.framework.context.support.TyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TyDefaultListableBeanFactory extends TyAbstractApplicationContext implements TyBeanFactory {

    //存储注册信息的BeanDefinition
    protected final Map<String,TyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,TyBeanDefinition>();

    @Override
    public Object getBean(String beanname) {
        return null;
    }

}
