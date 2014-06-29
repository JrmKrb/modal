import java.io.IOException;

import network.TCPServer;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
//		TCPClient client = new TCPClient("129.104.252.48", (short) 1337);
		TCPServer TCPserver = new TCPServer();
		TCPserver.start();
//		client.start();
//		
//		
//		Thread.sleep(3000);
//
//
//		client.serializedTask(new sumTask());
//		
//		
//		Thread.sleep(3000);
//		client.execute();
//		client.sendBuff();
		
		// TCPClient.sendTask("bin/application/sumTask.class");

	}
}