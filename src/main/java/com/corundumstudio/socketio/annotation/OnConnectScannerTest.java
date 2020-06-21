package com.corundumstudio.socketio.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.handler.SocketIOException;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.namespace.Namespace;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OnConnectScannerTest {
	OnConnectScanner onConnectScanner;

	/**
	*Purpose: To verify that all branch statements are executed
	*Input: 
	*	1. first if statements
	*		Method with 0 argument
	*		Method with 1 argument
	*		Method with 2 arguments
	*	2. second if statements
	*		Method with parameters of type SocketIOClient
	*		Method with no arguments of type SocketIOClient
	*	3. third if statements
	*		isValid = true
	*		isValid = false
	*Expected:
	*	1. first if statements
	*		Method with 0 argument => IllegalArgumentException
	*		Method with 1 argument => pass
	*		Method with 2 arguments => IllegalArgumentException
	*	2. second if statements
	*		Method with parameters of type SocketIOClient => isValid = true
	*		Method with no arguments of type SocketIOClient => isValid = false
	*	3. third if statements
	*		isValid = true => pass
	*		isValid = false => IllegalArgumentException
	*/
	
	public void testForMethod1() {
		;
	}
	public void testForMethod2(SocketIOClient socketIOClient) {
		;
	}
	public void testForMethod3(int a, double b) {
		;
	}
	
	@Test
	void testValidateListener() throws NoSuchMethodException {
		Method method1 = OnConnectScannerTest.class.getDeclaredMethod("testForMethod1");	
		Method method2 = OnConnectScannerTest.class.getDeclaredMethod("testForMethod2", SocketIOClient.class);
		Method method3 = OnConnectScannerTest.class.getDeclaredMethod("testForMethod3", int.class, double.class);
		
		onConnectScanner.validateListener(method1, int.class);
		onConnectScanner.validateListener(method2, int.class);
		onConnectScanner.validateListener(method3, int.class);
		
	}

}
