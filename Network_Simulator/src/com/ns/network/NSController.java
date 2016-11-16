package com.ns.network;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.LongAdder;

public class NSController extends NetworkEntity implements Runnable {
	private Bandwidth bandwidth = null;
	
	private BlockingQueue<Packet> innerQueue = new LinkedBlockingQueue<Packet>();
	private BlockingQueue<Packet> outerQueue = new LinkedBlockingQueue<Packet>();
	private BlockingQueue<Integer> selectionQueue = new LinkedBlockingQueue<Integer>(); // 0: InnterQueue, 1: OuterQueue
	
	private HashMap<String, NSSwitch> mySwitches = new HashMap<String, NSSwitch>();
	
	private LongAdder packetIn = new LongAdder();
	private LongAdder packetOut = new LongAdder();
	
	public NSController(String name) {
		super(name);
		bandwidth = new Bandwidth(this.getName(), 10);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		CoreNetwork.registerController(this);
		
		while (this.operable) {
			sendMessage();
			
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
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
	
	class SendingThread extends Thread {
		protected NSController cntr = null;
		
		public SendingThread(NSController cntr) {
			this.cntr = cntr;
		}
	}
	
	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		new SendingThread(this) {
			@Override
			public void run() {
				try {
					bandwidth.useResource();
					
					Packet pckt;
					boolean treated = false;
					int queueSelected = selectionQueue.take();
					
					if (queueSelected == 0) {
						pckt = innerQueue.take();
						for (NSSwitch swtch : mySwitches.values()) {
							if (swtch.containsHostByName(pckt.dst)) {
								pckt.touch(cntr.getName());
								swtch.receiveMessage(pckt);
								treated = true;
							}
						}
					}
					
					// When the target switch is migrated, the packet cannot be treated internally
					if (!treated) {
						pckt = outerQueue.take();
						pckt.touch(cntr.getName());
						CoreNetwork.requestBroadcastExcept(cntr, pckt);
					}
					
					packetOut.increment();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void receiveMessage(Packet pckt) {
		// TODO Auto-generated method stub
		try {
			boolean treated = false;
			
			for (NSSwitch swtch : mySwitches.values()) {
				if (swtch.containsHostByName(pckt.dst)) {
					innerQueue.put(pckt);
					selectionQueue.put(0);
					treated = true;
				}
			}
			
			if (!treated) {
				for (NSSwitch swtch : mySwitches.values()) {
					if (swtch.containsHostByName(pckt.src)) {
						outerQueue.put(pckt);
						selectionQueue.put(1);
						treated = true;
					}
				}
			}
			
			if (treated)
				packetIn.increment();
			
			if (!treated) {
				; // Drop
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getQueueLength() {
		String res = selectionQueue.size() + " (+" + packetIn.sumThenReset() + ", -" + packetOut.sumThenReset() + ")";
		return res;
	}
}
