package com.tianyou.spring.framework.demo.action;


import com.tianyou.spring.framework.annotation.TyAutowired;
import com.tianyou.spring.framework.annotation.TyController;
import com.tianyou.spring.framework.annotation.TyRequestMapping;
import com.tianyou.spring.framework.annotation.TyRequestParam;
import com.tianyou.spring.framework.demo.service.IModifyService;
import com.tianyou.spring.framework.demo.service.IQueryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author Tom
 *
 */
@TyController
@TyRequestMapping("/web")
public class MyAction {

	@TyAutowired
	IQueryService queryService;
	@TyAutowired
	IModifyService modifyService;

	@TyRequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
					  @TyRequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@TyRequestMapping("/add*.json")
	public void add(HttpServletRequest request,HttpServletResponse response,
			   @TyRequestParam("name") String name,@TyRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@TyRequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @TyRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@TyRequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@TyRequestParam("id") Integer id,
			@TyRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
