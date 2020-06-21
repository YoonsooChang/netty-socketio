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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.MultiTypeArgs;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.handler.SocketIOException;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.MultiTypeEventListener;
import com.corundumstudio.socketio.namespace.Namespace;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OnEventScannerTest {
	
	OnEventScanner onEventScanner;

	public void testForMethod1() {
		;
	}
	public void testForMethod2(SocketIOClient socketIOClient) {
		;
	}
	public void testForMethod3(AckRequest ackRequest) {
		;
	}
	public void testForMethod4(SocketIOClient socketIOClient, AckRequest ackRequest, int a) {
		;
	}
	
	/**
	*Purpose: To check if the if statement is executed according to the condition,
	*		and to check whether the method returns an appropriate value.
	*Input: 
	*	Method with 0 argument
	*	Method with 1 argument(SocketIOClient / AckRequest)
	*	Method with 2 arguments(SocketIOClient, AckRequest, int)
	*Expected:
	*	Method with 0 argument => null
	*	Method with 1 argument(SocketIOClient / AckRequest) => null
	*	Method with 2 arguments(SocketIOClient, AckRequest, int) => {2}
	*/
	@Test
	void testDataIndexes() {
		Method method1 = OnEventScannerTest.class.getDeclaredMethod("testForMethod1");	
		Method method2 = OnEventScannerTest.class.getDeclaredMethod("testForMethod2", SocketIOClient.class);
		Method method3 = OnEventScannerTest.class.getDeclaredMethod("testForMethod3", AckRequest.class);
		Method method4 = OnEventScannerTest.class.getDeclaredMethod("testForMethod4", SocketIOClient.class, AckRequest.class, int.class);
		
		assertNull(onEventScanner.dataIndexes(method1));
		assertNull(onEventScanner.dataIndexes(method2));
		assertNull(onEventScanner.dataIndexes(method3));
		
		List<Integer> expectedResult = new ArrayList();
		expectedResult.add(2);
		assertEquals(expectedResult, onEventScanner.dataIndexes(method4));
	}

	/**
	*Purpose: To check if the if statement is executed according to the condition,
	*		and to check whether the method returns an appropriate value.
	*Input: 
	*	Method with 0 argument
	*	Method with 1 argument(SocketIOClient / AckRequest)
	*	Method with 2 arguments(SocketIOClient, AckRequest, int)
	*Expected:
	*	Method with 0 argument => 0
	*	Method with 1 argument(SocketIOClient / AckRequest) => 0
	*	Method with 2 arguments(SocketIOClient, AckRequest, int) => 0
	*/
	@Test
	void testParamIndex() {
		Method method1 = OnEventScannerTest.class.getDeclaredMethod("testForMethod1");	
		Method method2 = OnEventScannerTest.class.getDeclaredMethod("testForMethod2", SocketIOClient.class);
		Method method3 = OnEventScannerTest.class.getDeclaredMethod("testForMethod3", AckRequest.class);
		Method method4 = OnEventScannerTest.class.getDeclaredMethod("testForMethod4", SocketIOClient.class, AckRequest.class, int.class);
		
		assertEquals(-1, onEventScanner.paramIndex(method1, SockectIOClient.class));
		assertEquals(-1, onEventScanner.paramIndex(method1, AckRequest.class));
		assertEquals(0, onEventScanner.paramIndex(method2, SockectIOClient.class));
		assertEquals(0, onEventScanner.paramIndex(method3, AckRequest.class));
		assertEquals(0, onEventScanner.paramIndex(method4, SockectIOClient.class));
		assertEquals(1, onEventScanner.paramIndex(method4, AckRequest.class));
	}

	/**
	*Purpose: To check that all branch statements are executed
	*Input: 
	*	1. first if statements
	*		Method with 0 argument
	*		Method with 1 argument(SocketIOClient / AckRequest)
	*		Method with 2 arguments(SocketIOClient, AckRequest, int)
	*	2. second if statements
	*		Method with 0 argument
	*		Method with 1 argument(SocketIOClient / AckRequest)
	*		Method with 2 arguments(SocketIOClient, AckRequest, int)
	*	3. third if statements
	*		Method with 0 argument
	*		Method with 1 argument(SocketIOClient / AckRequest)
	*		Method with 2 arguments(SocketIOClient, AckRequest, int)
	*Expected:
	*	1. first if statements
	*		Method with 0 argument => false
	*		Method with 1 argument(SocketIOClient / AckRequest) => true/false
	*		Method with 2 arguments(SocketIOClient, AckRequest, int) => true
	*	2. second if statements
	*		Method with 0 argument => false
	*		Method with 1 argument(SocketIOClient / AckRequest) => false/true
	*		Method with 2 arguments(SocketIOClient, AckRequest, int) => true
	*	3. third if statements
	*		Method with 0 argument => IllegalArgumentException
	*		Method with 1 argument(SocketIOClient / AckRequest) => pass
	*		Method with 2 arguments(SocketIOClient, AckRequest, int) => pass 
	*/
	
	@Test
	void testValidateListener() throws NoSuchMethodException {
		Method method1 = OnEventScannerTest.class.getDeclaredMethod("testForMethod1");	
		Method method2 = OnEventScannerTest.class.getDeclaredMethod("testForMethod2", SocketIOClient.class);
		Method method3 = OnEventScannerTest.class.getDeclaredMethod("testForMethod3", AckRequest.class);
		Method method4 = OnEventScannerTest.class.getDeclaredMethod("testForMethod4", SocketIOClient.class, AckRequest.class, int.class);
		
		onEventScanner.validateListener(method1, int.class);
		onEventScanner.validateListener(method2, int.class);
		onEventScanner.validateListener(method3, int.class);
		onEventScanner.validateListener(method4, int.class);
	}
}
