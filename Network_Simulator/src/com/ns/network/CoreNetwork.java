package com.ns.network;

import java.util.HashSet;

public class CoreNetwork extends Thread {
	private static HashSet<NSController> controllerset = new HashSet<NSController>();
	
	public static synchronized void registerController(NSController cntr) {
		controllerset.add(cntr);
	}
	
	public static synchronized void unregisterController(NSController cntr) {
		controllerset.remove(cntr);
	}
	
	public static synchronized void requestBroadcastExcept(NSController src_cntr, Packet pckt) {
		pckt.touch("CORE");
		
		for (NSController cntr : controllerset) {
			if (!cntr.equals(src_cntr)) {
				cntr.receiveMessage(pckt);
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (NSController cntr : controllerset) {
				System.out.println("[MONITOR] " + cntr.getName() + " queued by " + cntr.getQueueLength());
			}
			System.out.println("---------------------------");
		}
	}
}
