package application;

import network.tcp.Server;
import network.tcp.TCPClient;
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
		
		Thread.sleep(1000);
		for (String ip : udp.getNetworkList().keySet()) {
			System.out.println("Ouverture d'un TCPClient vers : "+ip);
			TCPClient client = new TCPClient((short) 1337, ip, classes, new TestTask());
			client.start();
		}

		System.out.println("Finished !");
	}

	/**
	 * @param readObject
	 */
	public static void treatResult(Task readObject) {
		TestTask temp = (TestTask) readObject;
		System.out.println("External calculus done. Result : " + temp.result);
	}
}