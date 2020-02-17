package com.tianyou.spring.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class TyViewResolver {

    private File templateRootDir;
    private String DEFAULT_TEMPLATE_SUFFX=".html";//默认模板后缀

    public TyViewResolver(String templateRoot){
        String templateRootPath=this.getClass().getClassLoader().getResource("templateRoot").getFile();
        templateRootDir=new File(templateRootPath);

    }

    /**
     *
     * @param viewname 视图名称
     * @param locale    将视图本地化（手写版本中不实现）
     * @return
     * @throws Exception
     */
    public TyView resolveViewName(String viewname, Locale locale) throws Exception{
        if(viewname.equals("") || viewname==null){
            return null;
        }

        //判断是否带html后缀名称，如果没有后缀则加后缀
        viewname=viewname.endsWith(DEFAULT_TEMPLATE_SUFFX)? viewname:(viewname+DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewname).replaceAll("/+","/"));
        return new TyView(templateFile);
    }
}
