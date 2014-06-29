package application;

import java.io.IOException;
import java.net.InetSocketAddress;
import network.TCPClient;
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

	}

	public static void treatResult(Task readObject) {
		testTask temp = (testTask) readObject;
		System.out.println("Le calcul distant a été effecuté. Le résultat est : " + temp.result);
	}
}