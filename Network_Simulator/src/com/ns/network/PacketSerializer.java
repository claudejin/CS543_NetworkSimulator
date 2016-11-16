package com.ns.network;

public class PacketSerializer {
	static int i = 1;
	
	public static synchronized String issueID() {
		String id = String.format("%04d", i);
		i++;
		
		return id;
	}
}
