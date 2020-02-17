package com.tianyou.spring.framework.demo.service.impl;


import com.tianyou.spring.framework.annotation.TyService;
import com.tianyou.spring.framework.demo.service.IModifyService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@TyService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception {
		throw new Exception("这是Tom老师故意抛的异常！！");
		//return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
