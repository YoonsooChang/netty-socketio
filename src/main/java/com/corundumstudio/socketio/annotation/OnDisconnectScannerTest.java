/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.corundumstudio.socketio.namespace.Namespace;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OnDisconnectScannerTest {
	OnDisconnectScanner onDisconnectScanner;

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
		Method method1 = OnDisconnectScannerTest.class.getDeclaredMethod("testForMethod1");	
		Method method2 = OnDisconnectScannerTest.class.getDeclaredMethod("testForMethod2", SocketIOClient.class);
		Method method3 = OnDisconnectScannerTest.class.getDeclaredMethod("testForMethod3", int.class, double.class);
		
		onDisconnectScanner.validateListener(method1, int.class);
		onDisconnectScanner.validateListener(method2, int.class);
		onDisconnectScanner.validateListener(method3, int.class);
		
	}

}
