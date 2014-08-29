package com.chengking.test.mybatis;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chengking.manager.IUserManager;

@RunWith(value = SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = {"classpath:/springconfig/applicationContext.xml"})
public class BaseMybatisTest extends TestCase{

	@Autowired  
	public IUserManager userMapper;
	
	@Before
	public void setup(){
		prepareTestData();
	}

	/**
	 * 准备单元测试数据
	 */
	private void prepareTestData() {
		
	}
}
