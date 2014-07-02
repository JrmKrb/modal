package application;

import network.tcp.Client;
import network.tcp.Server;
import network.udp.UDPConsumer;
import tasks.Task;
import tasks.TestTask;

public class TestConsumer {

	/**
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		// Server in order to get the result
		Server TCPserver = new Server();
		TCPserver.start();
		
		// IP of a supplier if broadcast does not work
		String supplierIP = "192.168.1.33";
		UDPConsumer udp = new UDPConsumer();
		udp.start();
		
		// Lists of the classes to send, the last one must be the task
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