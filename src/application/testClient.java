package application;

import java.net.InetSocketAddress;
import network.tcp.Server;
import network.tcp.TCPClient;
import tasks.Task;
import tasks.testTask;

public class TestClient {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Server in order to get the result
		Server TCPserver = new Server();
		TCPserver.start();

		String[] classes = {"bin/tasks/Doublet.class","bin/tasks/Task.java"};
		
		TCPClient client = new TCPClient((short) 1337, "192.168.1.27", classes);
		client.start();

	}

	/**
	 * @param readObject
	 */
	public static void treatResult(Task readObject) {
		testTask temp = (testTask) readObject;
		System.out.println("External calculus done. Result : " + temp.result);
	}
}