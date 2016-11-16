package com.ns.simulation;

import java.util.ArrayList;

import com.ns.network.NSController;
import com.ns.network.NSHost;
import com.ns.network.NSSwitch;
import com.ns.network.NetworkEntity;

public class Main {
	private ArrayList<NSHost> hosts = new ArrayList<NSHost>();
	private ArrayList<NSSwitch> switches = new ArrayList<NSSwitch>();
	private ArrayList<NSController> controllers = new ArrayList<NSController>();
	
	private ArrayList<NetworkEntity> entities = new ArrayList<NetworkEntity>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}
	
	public Main() {
		initialize();
		
		for (Runnable r : entities) {
			new Thread(r).start();
		}
		
		RandomMessageGenerator rmg = new RandomMessageGenerator(hosts);
		rmg.generate(5);
	}

	public void initialize() {
		entities.clear();
		hosts.clear();
		switches.clear();
		controllers.clear();
		
		NSController cntr1 = new NSController("c1"); controllers.add(cntr1);
		NSController cntr2 = new NSController("c2"); controllers.add(cntr2);
		NSController cntr3 = new NSController("c3"); controllers.add(cntr3);
		
		NSSwitch swtch1 = new NSSwitch("s1"); swtch1.setController(cntr1); switches.add(swtch1); 
		NSSwitch swtch2 = new NSSwitch("s2"); swtch2.setController(cntr1); switches.add(swtch2);
		NSSwitch swtch3 = new NSSwitch("s3"); swtch3.setController(cntr2); switches.add(swtch3);
		NSSwitch swtch4 = new NSSwitch("s4"); swtch4.setController(cntr3); switches.add(swtch4);
		NSSwitch swtch5 = new NSSwitch("s5"); swtch5.setController(cntr3); switches.add(swtch5);
		
		NSHost tmpHost = null;
		for (int i = 0; i < 15; i++) {
			tmpHost = new NSHost(String.format("h%02d", i+1));
			tmpHost.setSwitch(switches.get(i/3));
			hosts.add(tmpHost);
		}
		
		entities.addAll(controllers);
		entities.addAll(switches);
		entities.addAll(hosts);
	}
}
