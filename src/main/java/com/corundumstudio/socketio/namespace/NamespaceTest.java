package com.corundumstudio.socketio.namespace;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.corundumstudio.socketio.Configuration;

import com.corundumstudio.socketio.SocketIOClient;

public class NamespaceTest {
	Namespace namespace; 
	
	/** Purpose : To check that Equals function return an appropriate value.  
	 * 
	 * 
	 * Input : 
	 * 			1. Namespace Object - same
	 *			2. Namespace Object - different
	 * 			3. null
	 * 			4. Other Object
	 * 
	 * 
	 * Output : 
	 * 			1. Namespace Object - same --> True
	 *			2. Namespace Object - different --> False
	 * 			3. null --> False
	 * 			4. Other Object  --> False
	 */
	@Test
	public void testEquals() {
		String name1 = "name1";
		String name2 = "name2";
		Object obj = new Object();
		Configuration configuration = null;
		Namespace namespace1 = new Namespace(name1,configuration);
		Namespace namespace2 = new Namespace(name2,configuration);
		
		assertTrue(namespace1.equals(namespace1));
		assertFalse(namespace1.equals(namespace2));
		assertFalse(namespace1.equals(null));
		assertFalse(namespace1.equals(obj));
	}
	
	/** Purpose : To check that methods which are adding object work correctly.    
	 * 
	 * 
	 * Input : ConnectListener Object 
	 * 		
	 * 
	 * Output : True
	 */
	@Test
	public void testAddClient() {
		String name = "name";
		Configuration configuration = null;
		SocketIOClient client = (SocketIOClient) new Object();
		namespace = new Namespace(name,configuration);
		namespace.addClient(client);
		assertNotNull(namespace.getAllClients());
		
	}
}