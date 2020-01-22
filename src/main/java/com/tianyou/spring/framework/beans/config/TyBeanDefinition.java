package com.tianyou.spring.framework.beans.config;

public class TyBeanDefinition {


    private String beanClassName;//bean类名
    private boolean lazyInit = false;//是否懒加载,bean懒加载是在调用getBean的时候bean才初始化，非懒加载是在IOC容器初始化时bean就初始化
    private String factoryBeanName;//类存在工厂中的名称
    private boolean isSingleton=true;//判断bena是否单例，这里为了简化，默认为单例

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }
}
