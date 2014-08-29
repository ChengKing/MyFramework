package com.chengking.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.chengking.manager.IUserManager;
import com.chengking.model.bo.User;

@Controller
public class UserController extends BaseController {
	
	@Autowired
	IUserManager userManager;
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response){
		User user = userManager.findByName("monkey");
		ModelAndView model = new ModelAndView(getListView());
		model.addObject("user",user);
		return model;
	}

}
