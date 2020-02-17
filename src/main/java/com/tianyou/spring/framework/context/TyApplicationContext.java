package com.tianyou.spring.framework.context;

import com.tianyou.spring.framework.annotation.TyAutowired;
import com.tianyou.spring.framework.annotation.TyController;
import com.tianyou.spring.framework.annotation.TyService;
import com.tianyou.spring.framework.aop.TyAopProxy;
import com.tianyou.spring.framework.aop.TyCglibAopProxy;
import com.tianyou.spring.framework.aop.TyJdkDynamicAopProxy;
import com.tianyou.spring.framework.aop.config.TyAopConfig;
import com.tianyou.spring.framework.aop.support.TyAdvisedSupport;
import com.tianyou.spring.framework.beans.TyBeanFactory;
import com.tianyou.spring.framework.beans.TyBeanWrapper;
import com.tianyou.spring.framework.beans.config.TyBeanDefinition;
import com.tianyou.spring.framework.beans.config.TyBeanPostProcessor;
import com.tianyou.spring.framework.beans.support.TyBeanDefinitionReader;
import com.tianyou.spring.framework.beans.support.TyDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class TyApplicationContext extends TyDefaultListableBeanFactory implements TyBeanFactory {

    //配置文件路径
    private String[] configLocations;
    private TyBeanDefinitionReader reader;

    //单例IOC容器缓存
    private Map<String,Object> singletonObjects=new ConcurrentHashMap<String,Object>();

    //通用IOC容器
    private Map<String,TyBeanWrapper> factoryBeanInstanceCache=new ConcurrentHashMap<>();


    public TyApplicationContext(String...configLocations){
        this.configLocations=configLocations;
        refresh();//初始化时触发定位加载注册的过程
    }

    @Override
    public Object getBean(String beanname) throws Exception {

        Object instance=null;

        TyBeanPostProcessor beanPostProcessor=new TyBeanPostProcessor();

        //AOP前置通知
        beanPostProcessor.postProcessBeforeInitialization(instance,beanname);

        instance=instantiateBean(beanname,this.beanDefinitionMap.get(beanname));

        //1.初始化
        TyBeanWrapper beanWrapper=new TyBeanWrapper(instance);

        //2.拿到BeanWrapper之后要把它保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanname,beanWrapper);

        //AOP后置通知
        beanPostProcessor.postProcessAfterInitialization(instance,beanname);
        //注入
        populateBean(beanname,new TyBeanDefinition(),beanWrapper);
        return this.factoryBeanInstanceCache.get(beanname).getWrappedInstance();
    }

    @Override
    public Object getBean(Class<?> beanclass) throws Exception {
        return getBean(beanclass.getName());
    }

    /**
     * 依赖注入：给bean的字段赋值
     * @param beanname
     * @param tyBeanDefinition
     * @param tyBeanWrapper
     */
    private void populateBean(String beanname, TyBeanDefinition tyBeanDefinition, TyBeanWrapper tyBeanWrapper) {
        Object instance=tyBeanWrapper.getWrappedInstance();

        Class<?> clazz=tyBeanWrapper.getWrappedClass();
        //判断只有加了注解的类才执行依赖注入
        if (!clazz.isAnnotationPresent(TyController.class)||clazz.isAnnotationPresent(TyService.class)){
            return;
        }

        //获得clazz中所有字段
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            //没有Autowired注解则忽略
            if(!field.isAnnotationPresent(TyAutowired.class)){
                continue;
            }
            TyAutowired autowired=field.getAnnotation(TyAutowired.class);
            String autowiredbeanname=autowired.value().trim();
            if (autowiredbeanname.equals("")){
                //如果没有手动设置beanname，则默认用类名作为beanname
                autowiredbeanname=field.getType().getName();
            }

            //参数为true时可以通过反射强行访问private修饰的字段
            field.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(autowiredbeanname)==null){
                    continue;
                }
                //这里打个问号，如何给字段赋值的？
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredbeanname).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过BeanDefinition中的类名，获取类的实例，并存入单例容器中
     * @param beanname
     * @param tyBeanDefinition
     * @return
     */
    private Object instantiateBean(String beanname, TyBeanDefinition tyBeanDefinition) {
        //1.拿到要实例化对象的类名
        String classname=tyBeanDefinition.getBeanClassName();
        //2.反射实例化，得到换一个对象
        Object instance=null;
        try {
            //容器中有则使用容器中的实例
            //假设默认就是单例
            if(this.singletonObjects.containsKey(classname)){
                instance=singletonObjects.get(classname);
            }else {
                Class<?> clazz=Class.forName(classname);
                instance=clazz.newInstance();
                TyAdvisedSupport config = instantionAopConfig(tyBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，闯将代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }
                this.singletonObjects.put(classname,instance);
                //根据类型也能注入
                this.singletonObjects.put(tyBeanDefinition.getFactoryBeanName(),instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    @Override
    public void refresh() {
        //1.定位配置文件
        reader=new TyBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition对象
        List<TyBeanDefinition> TyBeanDefinitions= null;
        try {
            TyBeanDefinitions = reader.loadBeanDefinitions();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //3.把配置信息放到容器中（伪IOC容器）
        doRegisterBeanDefinition(TyBeanDefinitions);
        //4.把不是延时加载的类提前初始化
        doAuowired();
    }

    private TyAdvisedSupport instantionAopConfig(TyBeanDefinition gpBeanDefinition) {
        TyAopConfig config = new TyAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new TyAdvisedSupport(config);
    }

    private TyAopProxy createProxy(TyAdvisedSupport config) {

        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new TyJdkDynamicAopProxy(config);
        }
        return new TyCglibAopProxy(config);
    }

    /**
     * 非lazy-init的bean初始化
     */
    private void doAuowired() {
        for(Map.Entry<String,TyBeanDefinition> beanDefinitionEntry:super.beanDefinitionMap.entrySet()){
            String beanname=beanDefinitionEntry.getKey();
            //非懒加载调用getbean初始化
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanname);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将beanDefinitions注册到容器
     * @param beanDefinitions
     */
    public void doRegisterBeanDefinition(List<TyBeanDefinition> beanDefinitions){
        for(TyBeanDefinition beanDefinition:beanDefinitions){
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }


    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
