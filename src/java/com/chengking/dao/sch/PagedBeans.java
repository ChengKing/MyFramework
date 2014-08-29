/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: MONKEY@2014年5月17日 下午11:37:16
 */

package com.chengking.dao.sch;

import java.util.ArrayList;
import java.util.List;

 /**
 * 分页封装对象集合 <BR>
 * 
 * @author ChengKing
 * @since MONKEY@2014年5月17日
 */
public class PagedBeans {

	/**
	 * 上一页
	 */
	private int prevPage;
	
	/**
	 * 当前页
	 */
	private int curPage;
	
	/**
	 * 下一页
	 */
	private int nextPage;
	
	/**
	 * 当前页结果集
	 */
	private List<?> list ;
	
	/**
	 * 总页数
	 */
	private int pageTotal;
	

	/**
	 * 开始记录数
	 */
	private int startPos;
	
	/**
	 * 每页最大记录数
	 */
	private int pageSize;

	/**
	 * 总记录数
	 */
	private int total;
	
	/**
	 * 当前页记录数
	 */
	private int thisPageTotal;

	/**
	 * 是否是首页
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月18日
	 */
	public boolean isFirst(){
		return curPage == 1;
	}
	
	/**
	 * 是否是末页
	 * 
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月18日
	 */
	public boolean isLast(){
		return curPage == pageTotal;
	}
	
	public PagedBeans(List<?> list ,int startPos,int total){
		this(list,startPos,20,total);
	}
	
	/**
	 * 
	 * @param list
	 * @param startPos
	 * @param maxSize
	 * @param total
	 */
	public PagedBeans(List<?> list,int startPos,int maxSize,int total){
		this(list,startPos,maxSize,-1,total);
	}

	/**
	 * 构造分页集合
	 * 
	 * @param list
	 * @param startPos
	 * @param maxSize
	 * @param pageNo
	 * @param total
	 */
	@SuppressWarnings("rawtypes")
	public PagedBeans(List<?> list,int startPos,int maxSize,int pageNo,int total){
		this.list = (list==null)? new ArrayList() : list;
		this.startPos = startPos;
		this.pageSize = maxSize;
		this.total = total;
		this.pageTotal = ((total % pageSize)==0)? (total / pageSize) :(total / pageSize) +1;
		this.pageTotal = list.size();
		if(pageNo == -1){
			pageNo = pageTotal - (total - startPos)/maxSize;
		}
		this.curPage = (pageNo == 0) ? 1 : pageNo;
		this.prevPage = (curPage == 1) ? 1 : curPage-1;
		this.nextPage = (curPage >= pageTotal )? pageTotal : curPage+1; 
	}

	public int getStartPos() {
		return startPos;
		
	}


	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}


	public int getPageSize() {
		return pageSize;
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	public int getPrevPage() {
		return prevPage;
	}


	public int getCurPage() {
		return curPage;
	}


	public int getNextPage() {
		return nextPage;
	}


	public int getPageTotal() {
		return pageTotal;
	}


	public int getTotal() {
		return total;
	}

	public int getThisPageTotal() {
		return thisPageTotal;
	}
	
	public List<?> getThisPageItem(){
		return list;
	}
}
