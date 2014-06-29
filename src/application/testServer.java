package application;

import java.net.InetSocketAddress;
import network.tcp.Server;
import network.udp.UDPConsumer;

public class TestServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UDPConsumer udpT = new UDPConsumer();
		udpT.start();
		Server TCPserver = new Server(new InetSocketAddress("129.104.221.49",12347));
		TCPserver.start();

	}
}