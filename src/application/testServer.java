package application;

import network.tcp.Server;
import network.udp.UDPConsumer;

public class TestServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UDPConsumer udpT = new UDPConsumer();
		udpT.start();
		Server TCPserver = new Server();
		TCPserver.start();

	}
}