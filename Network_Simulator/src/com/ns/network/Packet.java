package com.ns.network;

public class Packet {
	public final String ID;
	public final String src;
	public final String dst;
	public final String msg;
	
	private String trace = "";
	private final long timestamp;
	
	public Packet(String src, String dst, String msg) {
		this.ID = PacketSerializer.issueID();
		this.src = src;
		this.dst = dst;
		this.msg = msg;
		this.timestamp = System.currentTimeMillis();
	}
	
	public String toString() {
		return "[MSG " + this.ID + "] " + this.src + " to " + this.dst + " " + msg + " " + this.getTrace() + " " + this.getAge();
	}
	
	public synchronized void touch(String name) {
		trace += "/" + name;
		//System.out.println("[TOUCH] " + toString());
	}
	
	public String getTrace() {
		return this.trace;
	}
	
	public long getAge() {
		return System.currentTimeMillis() - this.timestamp;
	}
}
