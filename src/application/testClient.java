package application;

import network.tcp.Server;
import network.tcp.TCPClient;
import tasks.Task;
import tasks.TestTask;

public class TestClient {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Server in order to get the result
		Server TCPserver = new Server();
		TCPserver.start();

		String[] classes = {"bin/tasks/Pair.class","bin/tasks/TestTask.class"};
		
		TCPClient client = new TCPClient((short) 1337, "129.104.221.49", classes, new TestTask());
		client.start();

	}

	/**
	 * @param readObject
	 */
	public static void treatResult(Task readObject) {
		TestTask temp = (TestTask) readObject;
		System.out.println("External calculus done. Result : " + temp.result);
	}
}