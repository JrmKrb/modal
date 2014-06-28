import network.TCPClient;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TCPClient client = new TCPClient("129.104.252.48", (short) 1337);
		client.start();
		Thread.sleep(500);
		
		client.error("TU ES UN GROS CACA");
		client.sendBuff();
		
		// TCPClient.sendTask("bin/application/sumTask.class");
	}
}