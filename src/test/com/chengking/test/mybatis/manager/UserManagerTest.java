package com.chengking.test.mybatis.manager;
import org.junit.Test;

import com.chengking.model.bo.User;
import com.chengking.test.mybatis.BaseMybatisTest;

public class UserManagerTest extends BaseMybatisTest  {  
    
	public void setup(){
		super.setup();
	}
      
    @Test
    public void testFindUser(){  
    	User entity = userMapper.findByName("monkey"); 
    	assertEquals("monkey", entity.getUserName());
    	
    }  
}  