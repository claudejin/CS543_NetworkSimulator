package com.ns.network;

import java.util.HashSet;

public class CoreNetwork extends Thread {
	private static HashSet<NEController> controllerSet = new HashSet<NEController>();
	private static HashSet<NESwitch> switchSet = new HashSet<NESwitch>();
	
	public static synchronized void registerController(NEController cntr) {
		controllerSet.add(cntr);
	}
	
	public static synchronized void unregisterController(NEController cntr) {
		controllerSet.remove(cntr);
	}
	
	public static synchronized void registerSwitch(NESwitch swtch) {
		switchSet.add(swtch);
	}
	
	public static synchronized void unregisterSwitch(NESwitch swtch) {
		switchSet.remove(swtch);
	}
	
	public static synchronized void requestBroadcastExcept(NEController src_cntr, Packet pckt) {
		pckt.touch("CORE");
		
		for (NEController cntr : controllerSet) {
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
			
			for (NEController cntr : controllerSet) {
				System.out.println("[MONITOR] " + cntr.getName() + " queued by " + cntr.getQueueLength());
			}
			for (NESwitch swtch : switchSet) {
				System.out.println("[MONITOR] " + swtch.getName() + " queued by " + swtch.getQueueLength());
			}
			System.out.println("---------------------------");
		}
	}
}
