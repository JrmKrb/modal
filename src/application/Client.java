package application;

import java.net.InetSocketAddress;
import java.util.HashMap;

import network.UDP;

public class Client {
	private static UDP UDPserver;
	
	public Client() {
		UDPserver = new UDP();
	}
	
	public void dispatchWork() {
		UDPserver.whoIsOnline();
		HashMap<InetSocketAddress, Integer> networkList = UDPserver.getNetworkList();
		int free = 0;
		for (InetSocketAddress isa : networkList.keySet()) {
			if (networkList.get(isa) == UDPserver.FREE)
				free++;
		}
	}
}
