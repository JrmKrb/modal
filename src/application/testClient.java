package application;

import java.io.IOException;
import java.net.InetSocketAddress;
import network.tcp.Server;
import network.tcp.TCPClient;
import tasks.Task;
import tasks.testTask;

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

		TCPClient client = new TCPClient((short) 1337, new InetSocketAddress("192.168.1.27",12348));
		client.start();
		
		Thread.sleep(1000);
		client.intro();
		client.error("TEST DEBUT");
		client.sendBuff();
		client.sendClass("bin/tasks/Doublet.class");
		client.sendTask("bin/tasks/testTask.class");
		client.sendSerializedTask(new testTask());
		client.execute();
		client.sendBuff();
		
		
	}

	public static void treatResult(Task readObject) {
		testTask temp = (testTask) readObject;
		System.out.println("Le calcul distant a été effecuté. Le résultat est : " + temp.result);
	}
}