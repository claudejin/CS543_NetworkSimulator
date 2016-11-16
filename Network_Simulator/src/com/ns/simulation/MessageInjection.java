package com.ns.simulation;

import com.ns.network.Packet;

public interface MessageInjection {
	public void enqueueMessage(Packet pckt);
}
