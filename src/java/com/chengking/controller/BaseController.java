package com.chengking.controller;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class BaseController extends MultiActionController {
	
	/**
	 * 成功视图
	 */
	private String successView;
	
	/**
	 * 列表视图
	 */
	private String listView;
	
	/**
	 * 详细列表视图
	 */
	private String detailView;

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public String getListView() {
		return listView;
	}

	public void setListView(String listView) {
		this.listView = listView;
	}

	public String getDetailView() {
		return detailView;
	}

	public void setDetailView(String detailView) {
		this.detailView = detailView;
	}
	
	

}
