package com.corundumstudio.socketio.store;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import com.corundumstudio.socketio.protocol.Packet;
import com.corundumstudio.socketio.protocol.PacketType;
import com.corundumstudio.socketio.store.MemoryStoreFactory;
import com.corundumstudio.socketio.store.pubsub.ConnectMessage;
import com.corundumstudio.socketio.store.pubsub.DisconnectMessage;
import com.corundumstudio.socketio.store.pubsub.DispatchMessage;
import com.corundumstudio.socketio.store.pubsub.JoinLeaveMessage;
import com.corundumstudio.socketio.store.pubsub.PubSubListener;
import com.corundumstudio.socketio.store.pubsub.PubSubMessage;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;
import com.corundumstudio.socketio.store.pubsub.PubSubType;

public class PubSubTest {

	static MemoryStoreFactory memstorefactory = new MemoryStoreFactory();
	static Store memStore;
	static PubSubStore pubStore;
	static UUID memSessionId = UUID.randomUUID();
	static UUID pubSessionId = UUID.randomUUID();
	static HashMap<PubSubType, PubSubMessage> msgs = new HashMap<PubSubType, PubSubMessage>();
	
	@BeforeClass
	public static void set() {
		memStore = memstorefactory.createStore(memSessionId);
		pubStore = memstorefactory.pubSubStore();   // MemoryPubSubStore
	}	
	
	@Before
	public void setTypes() {
		msgs.put(PubSubType.CONNECT, new ConnectMessage(pubSessionId));
		msgs.put(PubSubType.DISCONNECT, new DisconnectMessage(pubSessionId));
		msgs.put(PubSubType.DISPATCH, new DispatchMessage("ROOM", new Packet(PacketType.OPEN), "NAMESPACE"));
		msgs.put(PubSubType.JOIN, new JoinLeaveMessage(pubSessionId, "ROOM", "NAMESPACE"));

	}
	
	
	public class PubSubListenerImpl implements PubSubListener<ConnectMessage>{
		public PubSubMessage msg = null;
		@Override
		public void onMessage(ConnectMessage data) {
				msg = data;
		}
		
	}
	
	/*
	 * Purpose :To Run MemoryPubSubStore Class Methods
	 * Input 	: PubSubType, PubSubMessage, PubSubListener
	 * 		
	 */
	@Test
	public void MemoryPubSubStoreMethodsTest() {
		PubSubListener listenerImpl = new PubSubListenerImpl();
		PubSubMessage msg = new ConnectMessage(pubSessionId);
		pubStore.publish(PubSubType.CONNECT, msg);
		pubStore.subscribe(PubSubType.CONNECT, listenerImpl, null);
		pubStore.unsubscribe(PubSubType.CONNECT);
		pubStore.shutdown();
	}
	
	/*
	 * Purpose : To Run MemoryStore Class Methods
	 * Input 	: String(Keys)
	 * Expected : memStore.get("KEY1") = Not Null
	 * 			 memStore.has("KEY1") = true
	 * 			memStore.has("KEY2") = false
	 */
	
	@Test
	public void MemoryStoreMethodsTest() {
		memStore.set("KEY1", new TestObj());
		memStore.set("KEY2", new TestObj());

		System.out.println(memStore.get("KEY1"));
		System.out.println(memStore.get("KEY2"));
		
		assertNotNull(memStore.get("KEY1") );
		
		assertTrue(memStore.has("KEY1"));
		
		memStore.del("KEY2");
		assertFalse(memStore.has("KEY2"));
	}

	
	@AfterClass
	public static void delete() {
		memStore = null;
		pubStore = null;
		memstorefactory = null;
	}
	
	public class TestObj{
		int i;
		boolean b;
		float f;
		
		public TestObj() {
			Random random = new Random();
			i = random.nextInt();
			b = random.nextBoolean();
			f = random.nextFloat();
		}
		
		public String toString() {
			return i+"_"+b+"_"+f;
		}
	}
	
}
