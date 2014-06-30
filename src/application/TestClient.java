package application;

import network.tcp.Client;
import network.tcp.Server;
import network.udp.UDPConsumer;
import tasks.Task;
import tasks.TestTask;

public class TestClient {

	/**
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {

		// Server in order to get the result
		Server TCPserver = new Server();
		TCPserver.start();
		
		UDPConsumer udp = new UDPConsumer();
		udp.start();
		
		String[] classes = {"bin/tasks/Pair.class","bin/tasks/TestTask.class"};
		Client TCPClient = new Client(classes, new TestTask(), udp.getNetworkList());
		
		Thread.sleep(1000);
		TCPClient.start();

		System.out.println("Everything is launched.");
	}

	/**
	 * @param readObject
	 */
	public static void treatResult(Task readObject) {
		TestTask temp = (TestTask) readObject;
		System.out.println("External calculus done. Result : " + temp.result);
	}
}