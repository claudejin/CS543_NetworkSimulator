package com.ns.network;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.LongAdder;

import com.ns.simulation.Properties;

public class NESwitch extends NetworkEntity implements Runnable, Migratable {
	private Bandwidth bandwidth = null;
	
	private BlockingQueue<Packet> innerQueue = new LinkedBlockingQueue<Packet>();
	private BlockingQueue<Packet> outerQueue = new LinkedBlockingQueue<Packet>();
	private BlockingQueue<Integer> selectionQueue = new LinkedBlockingQueue<Integer>(); // 0: InnterQueue, 1: OuterQueue
	
	private NEController myController = null;
	private HashMap<String, NEHost> myHosts = new HashMap<String, NEHost>();
	
	private LongAdder packetIn = new LongAdder();
	private LongAdder packetOut = new LongAdder();

	public NESwitch(String name) {
		super(name);
		bandwidth = new Bandwidth(this.getName(), Properties.NE_SWITCH, Properties.SWITCH_BANDWIDTH);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		CoreNetwork.registerSwitch(this);
		
		while (this.operable) {
			sendMessage();
			
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		CoreNetwork.unregisterSwitch(this);
	}

	public void setController(NEController cntr) {
		this.myController = cntr;
		cntr.registerSwitch(this);
	}
	
	public void detachController() {
		myController.unregisterSwitch(this);
		this.myController = null;
	}
	
	public void registerHost(NEHost hst) {
		myHosts.put(hst.getName(), hst);
	}
	
	public void unregisterHost(NEHost hst) {
		if (myHosts.containsKey(hst.getName()))
			myHosts.remove(hst.getName());
		else
			System.out.println("ERR: Switch - unregisterHost");
	}
	
	public boolean containsHostByName(String hst_name) {
		return myHosts.containsKey(hst_name);
	}
	
	class SendingThread extends Thread {
		protected NESwitch swtch = null;
		
		public SendingThread(NESwitch swtch) {
			this.swtch = swtch;
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
					
					Packet pckt = null;
					boolean treated = false;
					int queueSelected = selectionQueue.take();
					
					// to-Host message (innerqueue)
					if (queueSelected == 0) {
						pckt = innerQueue.take();
						if (myHosts.containsKey(pckt.dst)) {
							pckt.touch(swtch.getName());
							myHosts.get(pckt.dst).receiveMessage(pckt);
							treated = true;
						}
					}
					
					if (!treated) {
						if (pckt == null)
							pckt = outerQueue.take();
						pckt.touch(swtch.getName());
						myController.receiveMessage(pckt);
					}
					
					packetOut.increment();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
			
			if (myHosts.containsKey(pckt.dst)) {
				innerQueue.put(pckt);
				selectionQueue.put(0);
				treated = true;
			}
			
			if (!treated) {
				if (myHosts.containsKey(pckt.src)) {
					outerQueue.put(pckt);
					selectionQueue.put(1);
					treated = true;
				}
			}
			
			if (treated)
				packetIn.increment();
			
			if (!treated)
				; // Drop
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void migrateTo(NetworkEntity target) {
		// TODO Auto-generated method stub
		NEController targetCntr = (NEController) target;
		NEController formerCntr = this.myController;
		
		targetCntr.registerSwitch(this);
		this.myController = targetCntr;
		formerCntr.unregisterSwitch(this);
	}

	public String getQueueLength() {
		String res = selectionQueue.size() + " (+" + packetIn.sumThenReset() + ", -" + packetOut.sumThenReset() + ")";
		return res;
	}
}
