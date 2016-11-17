package com.ns.simulation;

public class Properties {
	// Entity type
	public static int NE_HOST = 0;
	public static int NE_SWITCH = 1;
	public static int NE_CONTROLLER = 2;
	
	// In-entity parameters
	public static int HOST_BANDWIDTH = 0; // packets per second
	public static int SWITCH_BANDWIDTH = 1; // packets per second
	public static int CONTROLLER_BANDWIDTH = 10; // packets per second
	
	// Inter-entity parameters
	public static int HOST_SWITCH_TRANSMISSION_DELAY = 0; // millisecond
	public static int SWITCH_CONTROLLER_TRANSMISSION_DELAY = 1000; // millisecond
	
	// Simulation parameters
	public static int PACKET_IN_GENERATION_INTERVAL = 100; // millisecond
	public static int PACKET_IN_RATE = 40; // packets per second
}
