package application;

import java.net.InetSocketAddress;
import network.tcp.Server;
import network.udp.UDPSupplier;

public class TestServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String ip = "192.168.0.199";
		UDPSupplier udpT = new UDPSupplier(new InetSocketAddress(ip, 12347));
		udpT.start();
		Server TCPserver = new Server(new InetSocketAddress(ip, 12347));
		TCPserver.start();

	}
}