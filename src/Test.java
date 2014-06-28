import network.TCPClient;
import network.TCPServer;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TCPServer TCPserver = new TCPServer();
		TCPserver.start();

	}
}