package com.ns.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ns.simulation.MessageInjection;

public class NEHost extends NetworkEntity implements Runnable, MessageInjection {
	private BlockingQueue<Packet> msgQueue = new LinkedBlockingQueue<Packet>();
	
	private NESwitch mySwitch = null;
	
	public NEHost(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (this.operable) {
			sendMessage();
			
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	public void setSwitch(NESwitch swtch) {
		this.mySwitch = swtch;
		mySwitch.registerHost(this);
	}
	
	public void detachSwitch(NESwitch swtch) {
		mySwitch.unregisterHost(this);
		this.mySwitch = null;
	}
	
	@Override
	public void enqueueMessage(Packet pckt) {
		// TODO Auto-generated method stub
		try {
			msgQueue.put(pckt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		try {
			Packet pckt = msgQueue.take();
			pckt.touch(this.getName());
			
			mySwitch.receiveMessage(pckt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void receiveMessage(Packet pckt) {
		// TODO Auto-generated method stub
		pckt.touch(this.getName());
//		System.out.println(pckt.toString());
	}
	
}
