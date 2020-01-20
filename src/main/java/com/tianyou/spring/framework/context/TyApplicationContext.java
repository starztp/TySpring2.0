package com.tianyou.spring.framework.context;

import com.tianyou.spring.framework.beans.TyBeanFactory;
import com.tianyou.spring.framework.beans.support.TyDefaultListableBeanFactory;

public class TyApplicationContext extends TyDefaultListableBeanFactory implements TyBeanFactory {

    @Override
    public Object getBean(String beanname) {
        return null;
    }

    @Override
    public void refresh() {
        super.refresh();
    }
}
