package com.ns.network;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NSSwitch extends NetworkEntity implements Runnable {
	private Queue<Packet> msgQueue = new ConcurrentLinkedQueue<Packet>();
	
	private NSController myController = null;
	private HashMap<String, NSHost> myHosts = new HashMap<String, NSHost>();

	public NSSwitch(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (this.operable) {
			if (!msgQueue.isEmpty())
				sendMessage();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setController(NSController cntr) {
		this.myController = cntr;
		cntr.registerSwitch(this);
	}
	
	public void detachController() {
		myController.unregisterSwitch(this);
		this.myController = null;
	}
	
	public void registerHost(NSHost hst) {
		myHosts.put(hst.getName(), hst);
	}
	
	public void unregisterHost(NSHost hst) {
		if (myHosts.containsKey(hst.getName()))
			myHosts.remove(hst.getName());
		else
			System.out.println("ERR: Switch - unregisterHost");
	}
	
	public boolean containsHostByName(String hst_name) {
		return myHosts.containsKey(hst_name);
	}
	
	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		if (!msgQueue.isEmpty()) {
			Packet pckt = msgQueue.poll();
			pckt.touch(this.getName());
			myController.receiveMessage(pckt);
		}
	}

	@Override
	public void receiveMessage(Packet pckt) {
		// TODO Auto-generated method stub
		boolean treated = false;
		
		if (myHosts.containsKey(pckt.dst)) {
			pckt.touch(this.getName());
			myHosts.get(pckt.dst).receiveMessage(pckt);
			treated = true;
		}
		
		if (!treated) {
			if (myHosts.containsKey(pckt.src)) {
				msgQueue.add(pckt);
				treated = true;
			}
		}
		
		if (!treated)
			; // Drop
	}

}
