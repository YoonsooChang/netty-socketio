package com.corundumstudio.socketio.scheduler;

public class TaskRunnable implements Runnable {
	private String id;
	public String called = null;
	public TaskRunnable(String id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			System.out.println(id);
			called  = id;
		}
	}

}
