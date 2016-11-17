package com.ns.simulation;

import java.util.ArrayList;

import com.ns.network.CoreNetwork;
import com.ns.network.NEController;
import com.ns.network.NEHost;
import com.ns.network.NESwitch;
import com.ns.network.NetworkEntity;

public class Main {
	private ArrayList<NEHost> hosts = new ArrayList<NEHost>();
	private ArrayList<NESwitch> switches = new ArrayList<NESwitch>();
	private ArrayList<NEController> controllers = new ArrayList<NEController>();
	
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
		
		CoreNetwork monitor = new CoreNetwork();
		monitor.start();
		
		int budget = 10000;
		
		while (budget > 0) {
			rmg.generate(Properties.PACKET_IN_RATE * Properties.PACKET_IN_GENERATION_INTERVAL / 1000);
			budget--;
//			break;
			try {
				Thread.sleep(Properties.PACKET_IN_GENERATION_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		for (NetworkEntity ne : entities) {
			ne.shutdown();
		}
	}

	public void initialize() {
		entities.clear();
		hosts.clear();
		switches.clear();
		controllers.clear();
		
		NEController cntr1 = new NEController("c1"); controllers.add(cntr1);
		NEController cntr2 = new NEController("c2"); controllers.add(cntr2);
		NEController cntr3 = new NEController("c3"); controllers.add(cntr3);
		NEController cntr4 = new NEController("c4"); controllers.add(cntr4);
		
		NESwitch swtch1 = new NESwitch("s1"); swtch1.setController(cntr1); switches.add(swtch1); 
		NESwitch swtch2 = new NESwitch("s2"); swtch2.setController(cntr2); switches.add(swtch2);
		NESwitch swtch3 = new NESwitch("s3"); swtch3.setController(cntr3); switches.add(swtch3);
		NESwitch swtch4 = new NESwitch("s4"); swtch4.setController(cntr4); switches.add(swtch4);
		NESwitch swtch5 = new NESwitch("s5"); swtch5.setController(cntr4); switches.add(swtch5);
		
		NEHost tmpHost = null;
		for (int i = 0; i < 15; i++) {
			tmpHost = new NEHost(String.format("h%02d", i+1));
			tmpHost.setSwitch(switches.get(i/3));
			hosts.add(tmpHost);
		}
		
		entities.addAll(controllers);
		entities.addAll(switches);
		entities.addAll(hosts);
	}
}
