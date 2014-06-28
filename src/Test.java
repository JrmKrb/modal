import network.TCPClient;
import network.TCPServer;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TCPClient client = new TCPClient("129.104.252.48", (short) 1337);
		client.start();
		Thread.sleep(3000);
		
		client.intro();
		client.error("TU ES UN GROS CACA");
		client.sendBuff();
		client.sendTask("bin/sumTask.class");
		client.serializedTask(new sumTask());
		client.sendBuff();
		client.execute();
		client.sendBuff();
		
		// TCPClient.sendTask("bin/application/sumTask.class");

		/*TCPServer TCPserver = new TCPServer();
		TCPserver.start();*/

	}
}