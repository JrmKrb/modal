import java.io.IOException;
import java.net.InetAddress;
import network.TCPClient;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {

		// TCPServer TCPserver = new TCPServer();
		// TCPserver.start();
		Thread.sleep(3000);
		
		TCPClient client = new TCPClient((short) 1337);
		client.start();
		Thread.sleep(3000);
		client.intro();
		client.error("DEBUT DE CONNEXION.");
		client.sendBuff();
		client.execute();
		client.sendBuff();

		// TCPClient.sendTask("bin/application/sumTask.class");

	}
}