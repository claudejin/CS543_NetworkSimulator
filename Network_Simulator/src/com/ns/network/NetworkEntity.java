package com.ns.network;

public abstract class NetworkEntity implements Runnable {
	private String name;
	
	protected boolean operable = true;
	
	public NetworkEntity(String name) {
		this.name = name;
	}
	
	public String getName() { return this.name; }
	public abstract void sendMessage();
	public abstract void receiveMessage(Packet pckt);
	
	public void shutdown() { this.operable = false; }
}
