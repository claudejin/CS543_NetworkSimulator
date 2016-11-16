package com.ns.network;

import java.util.concurrent.Semaphore;

public class Bandwidth extends Semaphore {
	String name = null;
	
	public Bandwidth(String name, int permits) {
		super(permits);
		this.name = name;
	}
	
	public void useResource() {
		try {
			acquire();
			
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		release();
	}
}
