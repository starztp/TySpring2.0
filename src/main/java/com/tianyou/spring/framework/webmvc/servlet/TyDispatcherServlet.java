package com.tianyou.spring.framework.webmvc.servlet;

import com.tianyou.spring.framework.annotation.TyController;
import com.tianyou.spring.framework.annotation.TyRequestMapping;
import com.tianyou.spring.framework.context.TyApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TyDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION="contextconfiglocation";

    private List<TyHandlerMapping> handlerMappings=new ArrayList<>();

    private Map<TyHandlerMapping,TyHandlerAdapter> handleradpters=new HashMap<TyHandlerMapping,TyHandlerAdapter>();

    private TyApplicationContext context;

    private List<TyViewResolver> viewResolvers=new ArrayList<TyViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req,resp);
        } catch (Exception e) {
            try {
                processDispatchResult(req,resp,new TyModelAndView("500"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1.从Request中获取URL，去映射一个HandlerMapping
        TyHandlerMapping handlerMapping=getHandler(req);

        //2.获取HandlerAdapter
        TyHandlerAdapter handlerAdapter=getHandlerAdapter(handlerMapping);

        //3.真正调用方法
        TyModelAndView modelAndView=handlerAdapter.handle(req,resp,handlerMapping);

        //将modelandview转化为html页面
        processDispatchResult(req, resp, modelAndView);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.初始化ApplicationContext
        context=new TyApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2.初始化SpringMVC9大组件
        this.initStrategies(context);

    }

    /**
     * 通过req获取HandlerMapping对象
     * @param req
     * @return
     * @throws Exception
     */
    private TyHandlerMapping getHandler(HttpServletRequest req) throws Exception{
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (TyHandlerMapping handler : this.handlerMappings) {
            try{
                Matcher matcher = handler.getPattern().matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                throw e;
            }
        }
        return null;
    }

    private TyHandlerAdapter getHandlerAdapter(TyHandlerMapping handlerMapping){
        if(this.handleradpters.isEmpty()){
            return null;
        }
        TyHandlerAdapter adapter=this.handleradpters.get(handlerMapping);
        if (adapter.supports(handlerMapping)) {
            return adapter;
        }
        return null;
    }

    /**
     * 将modelandview对象转化为HTML、OuputStream、json、freemark、veolcity等
     * @param req
     * @param resp
     * @param modelAndView
     */
    private void processDispatchResult(HttpServletRequest req,HttpServletResponse resp,TyModelAndView modelAndView) throws Exception {
        if(modelAndView==null){
            return;
        }

        if (this.viewResolvers.isEmpty()){
            return;
        }

        for(TyViewResolver viewResolver:this.viewResolvers){
            TyView view=viewResolver.resolveViewName(modelAndView.getViewName(),null);
            view.render(modelAndView.getModel(),req,resp);
        }

    }

    protected void initStrategies(TyApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping
        try {
            initHandlerMappings(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化参数适配器
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
        //
        initFlashMapManager(context);
    }

    private void initMultipartResolver(TyApplicationContext context) {
    }

    private void initLocaleResolver(TyApplicationContext context) {
    }

    private void initThemeResolver(TyApplicationContext context) {
    }

    private void initHandlerMappings(TyApplicationContext context) throws Exception {
        String[] beannames=context.getBeanDefinitionNames();
        for(String beanname:beannames){
            Object obj=context.getBean(beanname);
            Class<?> clazz=obj.getClass();
            //因为handler的作用是要关联controller和Method,所以不是controller就忽略
            if(clazz.isAnnotationPresent(TyController.class)){
                continue;
            }
            String baseUrl = "";
            //获取Controller的url配置
            if(clazz.isAnnotationPresent(TyRequestMapping.class)){
                TyRequestMapping requestMapping = clazz.getAnnotation(TyRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //获取Method的url配置
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {

                //没有加RequestMapping注解的直接忽略
                if(!method.isAnnotationPresent(TyRequestMapping.class)){ continue; }

                //映射URL
                TyRequestMapping requestMapping = method.getAnnotation(TyRequestMapping.class);
                //  /demo/query

                //  (//demo//query)

                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);

                this.handlerMappings.add(new TyHandlerMapping(pattern,obj,method));
                log.info("Mapped " + regex + "," + method);
            }
        }
    }

    /**
     * 将一个Request请求转化成Handler
     * 把Request中的字符串形式的请求参数匹配到Handler中的形参
     * @param context
     */
    private void initHandlerAdapters(TyApplicationContext context) {
        for(TyHandlerMapping handlerMapping:handlerMappings){
            this.handleradpters.put(handlerMapping,new TyHandlerAdapter());
        }
    }

    private void initHandlerExceptionResolvers(TyApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(TyApplicationContext context) {
    }

    private void initViewResolvers(TyApplicationContext context) {
        //获取模板存放目录
        String templateRoot=context.getConfig().getProperty("templateRoot");
        String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir=new File(templateRootPath);
        for(File file:templateRootDir.listFiles()){

        }
    }

    private void initFlashMapManager(TyApplicationContext context) {
    }


}
