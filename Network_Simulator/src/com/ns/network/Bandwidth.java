package com.ns.network;

import java.util.concurrent.Semaphore;

import com.ns.simulation.Properties;

public class Bandwidth extends Semaphore {
	public final String name;
	public final int entityType;
	public final int transmissionDelay;
	
	public Bandwidth(String name, int entityType, int permits) {
		super(permits);
		this.entityType = entityType;
		this.name = name;
		
		if (entityType == Properties.NE_SWITCH)
			this.transmissionDelay = Properties.HOST_SWITCH_TRANSMISSION_DELAY;
		else if (entityType == Properties.NE_CONTROLLER)
			this.transmissionDelay = Properties.SWITCH_CONTROLLER_TRANSMISSION_DELAY;
		else
			this.transmissionDelay = 0;
	}
	
	public void useResource() {
		try {
			super.acquire();
			
//			System.out.println("Bandwidth " + name + " - " + this.availablePermits());
			Thread.sleep(this.transmissionDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			super.release();
//			System.out.println("Bandwidth " + name + " - " + this.availablePermits());
		}
	}
}
