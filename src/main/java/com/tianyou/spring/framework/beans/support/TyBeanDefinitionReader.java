package com.tianyou.spring.framework.beans.support;

import com.tianyou.spring.framework.beans.config.TyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by tianyou on 2020/1/21.
 */
public class TyBeanDefinitionReader {

    private Properties config = new Properties();

    private final String Scan_Package = "scanPackage";

    //用于存放包路径下类名
    List<String> registryBeannClasses = new ArrayList<String>();

    public TyBeanDefinitionReader(String... locations) {
        //通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(Scan_Package));
    }


    /**
     * 扫描包路径
     *并把包路径下的class文件名称存储到容器里
     * @param packagepath 传包路径
     */
    private void doScanner(String packagepath) {
        //将包路径转为系统文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + packagepath.replaceAll("\\.", "/"));
        File filepath = new File(url.getFile());
        for (File file : filepath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packagepath + "." + file.getName());
            }

            //只要class文件
            if (!(file.getName().endsWith(".class"))) {
                continue;
            }
            String classname = file.getName().replace(".class", "");
            registryBeannClasses.add(classname);
        }
    }

    /**
     *
     * @return
     */
    public List<TyBeanDefinition> loadBeanDefinitions(){
        List<TyBeanDefinition> definitionList=new ArrayList<>();
        for(String classname:registryBeannClasses){
            TyBeanDefinition beanDefinition=doCreateBeanDefinition(classname);
            if (beanDefinition==null){
                continue;
            }
            definitionList.add(beanDefinition);
        }
        return definitionList;
    }


    /**
     * 将class对象转化为BeanDefinition对象
     * @param classname
     * @return
     */
    public TyBeanDefinition doCreateBeanDefinition(String classname){
        TyBeanDefinition beanDefinition=new TyBeanDefinition();
        try {
            Class<?> beanclass=Class.forName(classname);
            //暂时忽略接口bean
            if(!beanclass.isInterface()){
                return null;
            }
            beanDefinition.setBeanClassName(classname);
            beanDefinition.setFactoryBeanName(beanclass.getSimpleName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return beanDefinition;
    }


    public Properties getConfig() {
        return config;
    }

    //如果类名本身是小写字母，确实会出问题
    //但是我要说明的是：这个方法是我自己用，private的
    //传值也是自己传，类也都遵循了驼峰命名法
    //默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况

    //为了简化程序逻辑，就不做其他判断了，大家了解就OK
    //其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}