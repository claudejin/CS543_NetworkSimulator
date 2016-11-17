package com.ns.simulation;

import java.util.ArrayList;
import java.util.Random;

import com.ns.network.NEHost;
import com.ns.network.Packet;

public class RandomMessageGenerator {
	private ArrayList<NEHost> hostPool = null;
	
	private int targetHost = -1;
	
	public RandomMessageGenerator(ArrayList<NEHost> hostPool) {
		this.hostPool = hostPool;
	}
	
	public void generate(int numPacket) {
		for (int i = 0; i < numPacket; i++) {
			targetHost = (targetHost + 1) % hostPool.size();
			NEHost src = hostPool.get(targetHost); //new Random().nextInt(hostPool.size())
			NEHost dst = hostPool.get(new Random().nextInt(hostPool.size()));
			
			MessageInjection mi = src;
			Packet pckt = new Packet(src.getName(), dst.getName(), "hello");
			
//			System.out.println("GEN " + pckt.toString());
			
			mi.enqueueMessage(pckt);
		}
	}
}
