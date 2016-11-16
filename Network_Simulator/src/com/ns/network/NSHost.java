package com.ns.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ns.simulation.MessageInjection;

public class NSHost extends NetworkEntity implements Runnable, MessageInjection {
	private Queue<Packet> msgQueue = new ConcurrentLinkedQueue<Packet>();
	
	private NSSwitch mySwitch = null;
	
	public NSHost(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
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
	
	public void setSwitch(NSSwitch swtch) {
		this.mySwitch = swtch;
		mySwitch.registerHost(this);
	}
	
	public void detachSwitch(NSSwitch swtch) {
		mySwitch.unregisterHost(this);
		this.mySwitch = null;
	}
	
	@Override
	public void enqueueMessage(Packet pckt) {
		// TODO Auto-generated method stub
		msgQueue.add(pckt);
	}

	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		if (!msgQueue.isEmpty()) {
			Packet pckt = msgQueue.poll();
			pckt.touch(this.getName());
			mySwitch.receiveMessage(pckt);
		}
	}

	@Override
	public void receiveMessage(Packet pckt) {
		// TODO Auto-generated method stub
		pckt.touch(this.getName());
		System.out.println(pckt.toString());
	}
	
}
