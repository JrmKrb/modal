import network.TCPClient;
import network.TCPServer;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TCPServer TCPserver = new TCPServer();
		TCPClient TCPClient = new TCPClient("127.0.0.1", (short) 1337);
		TCPserver.start();
		Thread.sleep(500);
		TCPClient.start();
		Thread.sleep(500);
		TCPClient.end();
		TCPClient.sendBuff();
		TCPClient.sendTask("bin/application/sumTask.class");
	}
}