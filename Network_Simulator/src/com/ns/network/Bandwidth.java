package com.ns.network;

import java.util.concurrent.Semaphore;

import com.ns.simulation.Properties;

public class Bandwidth extends Semaphore {
	String name = null;
	
	public Bandwidth(String name, int permits) {
		super(permits);
		this.name = name;
	}
	
	public void useResource() {
		try {
			super.acquire();
			
//			System.out.println("Bandwidth " + name + " - " + this.availablePermits());
			Thread.sleep(Properties.SWITCH_CONTROLLER_TRANSMISSION_DELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			super.release();
//			System.out.println("Bandwidth " + name + " - " + this.availablePermits());
		}
	}
}
