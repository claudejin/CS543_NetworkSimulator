package com.ns.simulation;

import java.util.ArrayList;
import java.util.Random;

import com.ns.network.NSHost;
import com.ns.network.Packet;

public class RandomMessageGenerator {
	private ArrayList<NSHost> hostPool = null;
	
	private int targetHost = 0;
	
	public RandomMessageGenerator(ArrayList<NSHost> hostPool) {
		this.hostPool = hostPool;
	}
	
	public void generate(int numPacket) {
		targetHost = (targetHost + 1) % hostPool.size();
		
		for (int i = 0; i < numPacket; i++) {
			NSHost src = hostPool.get(targetHost); //new Random().nextInt(hostPool.size())
			NSHost dst = hostPool.get(new Random().nextInt(hostPool.size()));
			
			MessageInjection mi = src;
			Packet pckt = new Packet(src.getName(), dst.getName(), "hello");
			
//			System.out.println("GEN " + pckt.toString());
			
			mi.enqueueMessage(pckt);
		}
	}
}
