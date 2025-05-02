package com.jlmorab.ms.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Helper that provides observability of the application's console out and err
 */
public class LoggerHelper { // NOSONAR
	
	private static final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
	private static final ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
	
	private	static PrintStream originalOut;
	private static PrintStream originalErr;
	
	private LoggerHelper() {}
	
	private static class Holder {
		private static final LoggerHelper INSTANCE = new LoggerHelper();
	}//end Holder
	
	
	public static LoggerHelper getInstance() {
		LoggerHelper instance = Holder.INSTANCE;
		LoggerHelper.originalOut = System.out; // NOSONAR
		LoggerHelper.originalErr = System.err; // NOSONAR
		instance.initCapture();
		return instance;
	}//end getInstance()
	
	public void initCapture() {
		capturedOut.reset();
		System.setOut( new PrintStream( capturedOut ) );
		capturedErr.reset();
		System.setErr( new PrintStream( capturedErr  ) );
	}//end initCapture()
	
	public void release() {
		System.setOut( originalOut );
		System.setErr( originalErr );
	}//end release()
	
	public String getOutContent() {
		return capturedOut.toString();
	}//end getOutContent()
	
	public String getErrContent() {
		return capturedErr.toString();
	}//end getErrContent()
	
	
	static void destroy() {
		System.setOut( originalOut );
	    System.setErr( originalErr );
	    capturedOut.reset();
	    capturedErr.reset();
	}//end destroy()

}
