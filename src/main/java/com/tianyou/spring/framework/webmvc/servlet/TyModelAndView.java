package com.tianyou.spring.framework.webmvc.servlet;

import java.util.Map;

public class TyModelAndView {

    private String viewName;//视图名称
    private Map<String,?> model;

    public TyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public TyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
