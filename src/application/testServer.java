package application;
import java.io.IOException;
import network.Server;
import network.UDPConsumer;

public class testServer {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {

		UDPConsumer udpT = new UDPConsumer();
		udpT.start();
		Server TCPserver = new Server();
		TCPserver.start();

	}
}