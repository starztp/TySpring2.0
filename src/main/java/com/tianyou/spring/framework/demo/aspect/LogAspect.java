package com.tianyou.spring.framework.demo.aspect;

public class LogAspect {

    public void before(){
        System.out.println("方法执行前调用");
    }

    public void after(){
        System.out.println("方法执行后调用");
    }

    public void afterThrowing(){
        System.out.println("方法执行有异常后调用");
    }
}
