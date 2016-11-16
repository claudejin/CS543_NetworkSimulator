package com.ns.network;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NSController extends NetworkEntity implements Runnable {
	private boolean operable = true;
	
	private Bandwidth bandwidth = null;
	
	private Queue<Packet> innerQueue = new ConcurrentLinkedQueue<Packet>();
	private Queue<Packet> outerQueue = new ConcurrentLinkedQueue<Packet>();
	private Queue<Integer> selectionQueue = new ConcurrentLinkedQueue<Integer>(); // 0: InnterQueue, 1: OuterQueue
	
	private HashMap<String, NSSwitch> mySwitches = new HashMap<String, NSSwitch>();
	
	public NSController(String name) {
		super(name);
		bandwidth = new Bandwidth(this.getName(), 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		CoreNetwork.registerController(this);
		
		while (operable) {
			if (!selectionQueue.isEmpty())
				sendMessage();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CoreNetwork.unregisterController(this);
	}
	
	public void registerSwitch(NSSwitch swtch) {
		mySwitches.put(swtch.getName(), swtch);
	}
	
	public void unregisterSwitch(NSSwitch swtch) {
		if (mySwitches.containsKey(swtch.getName()))
			mySwitches.remove(swtch.getName());
		else
			System.out.println("ERR: Controller - unregisterSwitch");
	}

	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		if (!selectionQueue.isEmpty()) {
			Packet pckt;
			int queueSelected = selectionQueue.poll();
			boolean treated = false;
			
			bandwidth.useResource();
			
			if (queueSelected == 0) {
				pckt = innerQueue.poll();
				for (NSSwitch swtch : mySwitches.values()) {
					if (swtch.containsHostByName(pckt.dst)) {
						pckt.touch(this.getName());
						swtch.receiveMessage(pckt);
						treated = true;
					}
				}
			}
			
			// When the target switch is migrated, the packet cannot be treated internally
			if (!treated) {
				pckt = outerQueue.poll();
				pckt.touch(this.getName());
				CoreNetwork.requestBroadcastExcept(this, pckt);
			}
		}
	}

	@Override
	public void receiveMessage(Packet pckt) {
		// TODO Auto-generated method stub
		boolean treated = false;
		
		for (NSSwitch swtch : mySwitches.values()) {
			if (swtch.containsHostByName(pckt.dst)) {
				innerQueue.add(pckt);
				selectionQueue.add(0);
				treated = true;
			}
		}
		
		if (!treated) {
			for (NSSwitch swtch : mySwitches.values()) {
				if (swtch.containsHostByName(pckt.src)) {
					outerQueue.add(pckt);
					selectionQueue.add(1);
					treated = true;
				}
			}
		}
		
		if (!treated) {
			; // Drop
		}
	}

}
