package application;

import java.net.InetSocketAddress;
import network.tcp.Server;
import network.udp.UDPConsumer;

public class TestServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UDPConsumer udpT = new UDPConsumer(new InetSocketAddress("192.168",12347));
		udpT.start();
		Server TCPserver = new Server(new InetSocketAddress("192.168",12347));
		TCPserver.start();

	}
}