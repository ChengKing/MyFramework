package com.chengking.manager;

import java.util.List;

import com.chengking.model.bo.User;

public interface IUserManager {
	/**
	 * 根据ID查询用户
	 * 
	 * @param id
	 * @return
	 */
	public User findById(int id);
	
	/**
	 * 根据用户名查询用户
	 * 
	 * @param userName
	 * @return
	 */
	public User findByName(String userName);
	
	/**
	 * 
	 * @param user
	 */
	public void addUser(User user);
	
	/**
	 * 
	 * @param userName
	 */
	public void deleteUser(String userName);
	
	/**
	 * 
	 * @param userName
	 */
	public List<User> listUser(String userName);
}
