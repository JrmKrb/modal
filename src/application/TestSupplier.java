package application;

import network.tcp.Server;
import network.udp.UDPSupplier;

public class TestSupplier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		// IF auto-IP doesn't work. IP is the IP of this computer (the supplier).
		/*String ip = "192.168.0.199";
		UDPSupplier udpT = new UDPSupplier(new InetSocketAddress(ip, 12347));
		udpT.start();
		Server TCPserver = new Server(new InetSocketAddress(ip, 12347));
		TCPserver.start();*/
		
		//If it works
		UDPSupplier udpT = new UDPSupplier();
		udpT.start();
		Server TCPserver = new Server();
		TCPserver.start();
		
		

	}
}