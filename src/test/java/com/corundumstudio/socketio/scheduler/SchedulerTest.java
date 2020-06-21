package com.corundumstudio.socketio.scheduler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.util.Timeout;
import jodd.util.concurrent.ThreadFactoryBuilder;

public class SchedulerTest {
	
	public static int EMPTY = 0;
	
	static private CancelableScheduler hws;
	static private CancelableScheduler hwt;
	static private ArrayList<SchedulerKey> keys = new ArrayList<SchedulerKey>();
	
	/**
	*Purpose : To Set ArrayList of 'SchedulerKey's.
	*		  (used as key of ScheduledFutures(Map Container) of Scheduler Classes).			 
	*/
	
	@BeforeClass
	public static void setKeys() throws Exception{
		SchedulerKey pingTimeoutKey = 
				new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT,
						'0');
		SchedulerKey ackTimeoutKey = 
				new SchedulerKey(SchedulerKey.Type.ACK_TIMEOUT,
						'1');
		SchedulerKey upgradeTimeoutKey = 
				new SchedulerKey(SchedulerKey.Type.UPGRADE_TIMEOUT,
						'2');
		
		keys.add(pingTimeoutKey);
		keys.add(ackTimeoutKey);
		keys.add(upgradeTimeoutKey);
	}
	
	/*
	 * Purpose : Assign CancelableScheduler instances with ThreadFactory constructor
	 */
	
	@Before
	public void setClasses() throws Exception{
		ThreadFactory tf = new ThreadFactoryBuilder().get();
		hws = new HashedWheelScheduler(tf);
		hwt = new HashedWheelTimeoutScheduler(tf);
	}

	
	/*
	 * Purpose : To Check whether Timer(HashedWheelTimer) of CancelableScheduler get
	 * 			a runnable TimerTask but ScheduledFuture of CancelableScheduler is not
	 * 			affected by method 
	 * Input 	: Thread(runnable), 
	 * 		  	  Timeout, 
	 *       	  Time Unit(Sec, MilliSec, MicroSec...) 
	 * Expected : hws.executorService.pendingTimeouts() = 1
	 * 			  scheduledFutures.size() = 0(EMPTY)
	 */
	@Test
	public void scheduleWithoutKeyTest() {
		TaskRunnable runnable = new TaskRunnable("TEST");
		
		hws.schedule(runnable, 2000, TimeUnit.MILLISECONDS );
		assertEquals(hws.executorService.pendingTimeouts(), 1);
		assertEquals(hws.scheduledFutures.size(), EMPTY);
	}
	
	
	/*
	 * Purpose : To Check Scheduling TimerTask Of HashedWheelScheduler and HashedWheelTimeoutScheduler
	 *  		 Checking whether scheduledFutures and executorService of instances are affected by this method
	 *  		 Make TimerTasks(threads) run and Check whether they runned properly by attribute 'called'
	 * Input 	: SchedulerKey
	 * 			  Thread(runnable), 
	 * 		  	  Timeout, 
	 *       	  Time Unit(Sec, MilliSec, MicroSec...) 
	 * Expected : {hws, hwt}.executorService.pendingTimeouts() = 1
	 * 			  {hws, hwt}.scheduledFutures.size() = 1
	 * 			   runnable1.called =  "HWS"
	 * 			   runnable2.called =  "HWS"
	 */
	@Test
	public void scheduleWithKeyTest() {
		TaskRunnable runnable1 = new TaskRunnable("HWS");
		TaskRunnable runnable2 = new TaskRunnable("HWT");

		hws.schedule(keys.get(1), runnable1, 2000, TimeUnit.MILLISECONDS );
		hwt.schedule(keys.get(2), runnable2, 3000, TimeUnit.MILLISECONDS );
	
		assertEquals(hws.scheduledFutures.size() , 1);
		assertEquals(hws.executorService.pendingTimeouts(), 1);

		assertEquals(hwt.scheduledFutures.size() , 1);
		assertEquals(hwt.executorService.pendingTimeouts(), 1);

		Timeout hwsTimeout = hws.scheduledFutures.get(keys.get(1));
		Timeout hwtTimeout = hwt.scheduledFutures.get(keys.get(2));
		
		try {
			hwsTimeout.task().run(hwsTimeout);
			hwtTimeout.task().run(hwtTimeout);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			assertEquals(runnable1.called, "HWS");
			assertEquals(runnable2.called, "HWT");
		}
		
	}
	
	/*
	 * Purpose : To Cancel Scheduled TimerTask 
	 * 			 Checking whether Timeout of the task is cancelled before and after calling cancel method.
	 * Input 	: Timeout
	 * Expected : hws.executorService.pendingTimeouts() = 1
	 * 			  scheduledFutures.size() = 1
	 * 			 (Before Cancel) hwtTimeout.isCancelled() = false
	 * 			 (After Cancel)  hwtTimeout.isCancelled() = true
	 * 							 hwtTimeout.cancel = false (Timeout is already cancelled)
	 */
	@Test
	public void cancelTest() throws Exception {
		TaskRunnable runnable = new TaskRunnable("TEST");
		
		hwt.schedule(keys.get(2), runnable, 2000, TimeUnit.MILLISECONDS );
		assertEquals(hwt.executorService.pendingTimeouts(), 1);
		assertEquals(hwt.scheduledFutures.size() , 1);
		
		Timeout hwtTimeout = hwt.scheduledFutures.get(keys.get(2));		
	
		assertFalse(hwtTimeout.isCancelled());
		
		hwt.cancel(keys.get(2));
		
		assertEquals(hwt.executorService.pendingTimeouts(), 1);
		assertEquals(hwt.scheduledFutures.size() , EMPTY);
		
		assertFalse(hwtTimeout.cancel());
		assertTrue(hwtTimeout.isCancelled());
	}
	
	
	/*
	 * Purpose : Turn Off Timer of Scheduler Instances and Delete instances;
	 * 
	 */ 
	@After
	public void shutdown() throws Exception {
		turnOffTimer();
		hws = null;
		hwt = null;
	}

	public void turnOffTimer() {
		hws.shutdown();
		hwt.shutdown();
	}
	
	
	/*
	 * Purpose : Clear SchedulerKey ArrayList;
	 * 
	 */ 
	@AfterClass
	public static void resetKeys() throws Exception{
		keys.clear();
	}

	
}
