package application;

import java.io.IOException;
import network.TCPClient;

public class testClient {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {

		// Server in order to get the result
		Server TCPserver = new Server();
		TCPserver.start();
		Thread.sleep(3000);

		TCPClient client = new TCPClient((short) 1337);
		client.start();
		Thread.sleep(3000);
		client.intro();
		client.error("DEBUT DE CONNEXION.");
		client.sendBuff();
		client.sendTask("bin/application/testTask.class");
		client.sendBuff();
		client.serializedTask(new testTask());
		client.sendBuff();
		client.execute();
		client.sendBuff();
		// TCPClient.sendTask("bin/application/sumTask.class");

	}

	public static void treatResult(Task readObject) {
		testTask temp = (testTask) readObject;
		System.out.println("Le calcul distant a été effecuté. Le résultat est : " + temp.result);
	}
}