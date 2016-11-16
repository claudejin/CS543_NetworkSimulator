package com.ns.network;

public abstract class NetworkEntity implements Runnable {
	private String name;
	
	public NetworkEntity(String name) {
		this.name = name;
	}
	
	public String getName() { return this.name; }
	public abstract void sendMessage();
	public abstract void receiveMessage(Packet pckt);
}
