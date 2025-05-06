package com.jlmorab.ms.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoggerHelperTest {
    
    @BeforeEach
    void setUp() {
        LoggerHelper.destroy();
    }//end setUp()
    
    @AfterEach
    void tearDown() {
        LoggerHelper.destroy();
    }//end tearDown()

    
	@Test
	void instance_isSingleton() {
		LoggerHelper instance1 = LoggerHelper.getInstance();
		LoggerHelper instance2 = LoggerHelper.getInstance();
		assertSame(instance1, instance2);
	}//end instance_isSingleton()
	
	@Test
	void initCapture_capturesOutput() {
		LoggerHelper instance = LoggerHelper.getInstance();
		instance.initCapture();
		
		System.out.println("Test output");
		System.err.println("Test error");
		
		String outContent = instance.getOutContent();
		String errContent = instance.getErrContent();
		
		assertTrue(outContent.contains("Test output"));
		assertTrue(errContent.contains("Test error"));
		
		instance.release();
	}//end initCapture_capturesOutput()
	
	@Test
	void initCapture_isolatesOutput() {
		LoggerHelper instance = LoggerHelper.getInstance();
		instance.initCapture();
		
		System.out.println("Other output");
		System.err.println("Other error");
		
		instance.initCapture();
		
		assertThat( instance.getOutContent() ).isEmpty();
	}//end initCapture_isolatesOutput()
	
}
