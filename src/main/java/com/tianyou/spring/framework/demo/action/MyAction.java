package com.gupaoedu.vip.spring.demo.action;

import com.tianyou.spring.framework.annotation.TyAutowired;
import com.tianyou.spring.framework.annotation.TyController;
import com.tianyou.spring.framework.annotation.TyRequestMapping;
import com.tianyou.spring.framework.annotation.TyRequestParam;
import com.tianyou.spring.framework.demo.service.IModifyService;
import com.tianyou.spring.framework.demo.service.IQueryService;
import com.tianyou.spring.framework.webmvc.servlet.TyModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



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
	public TyModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@TyRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}

	@TyRequestMapping("/add*.json")
	public TyModelAndView add(HttpServletRequest request,HttpServletResponse response,
							  @TyRequestParam("name") String name,@TyRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new TyModelAndView("500",model);
		}

	}

	@TyRequestMapping("/remove.json")
	public TyModelAndView remove(HttpServletRequest request,HttpServletResponse response,
								 @TyRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}

	@TyRequestMapping("/edit.json")
	public TyModelAndView edit(HttpServletRequest request,HttpServletResponse response,
							   @TyRequestParam("id") Integer id,
							   @TyRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}



	private TyModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
