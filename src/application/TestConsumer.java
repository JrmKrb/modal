package application;

import java.util.LinkedList;

import network.tcp.Client;
import network.tcp.Server;
import network.udp.UDPConsumer;
import tasks.Task;
import tasks.TestTask;
import tasks.TestTaskWithParameters;

public class TestConsumer {

	static long result = 1L;
	static int p = 104729;

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
		LinkedList<String> suppliersIP = new LinkedList<String>();
		suppliersIP.add("129.104.252.45");
		suppliersIP.add("129.104.252.47");
		UDPConsumer udp = new UDPConsumer(suppliersIP);
		// UDPConsumer udp = new UDPConsumer();
		udp.start();

		// Lists of the classes to send, the last one must be the task

		/*
		 * TEST 1
		 */
		// String[] classes =
		// {"bin/tasks/Pair.class","bin/tasks/TestTask.class"};
		// Client TCPClient = new Client(classes, new TestTask(),
		// udp.getNetworkList());

		/*
		 * TEST 2
		 */
		String[] classes = { "bin/tasks/TestTaskWithParameters.class" };
		Task[] tasks = { new TestTaskWithParameters(1, 5, p),
				new TestTaskWithParameters(6, 100000, p) };
		Client TCPClient = new Client(classes, tasks, udp.getNetworkList());

		Thread.sleep(1000);
		TCPClient.start();

		System.out.println("Everything is launched.");

		long res2 = 1;
		for (int k = 1; k <= 100000; k++) {
			res2 = (res2 * k) % p;
		}
		System.out.println("Resultat local : "+res2);

	}

	/**
	 * @param readObject
	 *            1
	 */
	/*
	 * public static void treatResult(Task readObject) { TestTask temp =
	 * (TestTask) readObject; result = temp.result;
	 * System.out.println("External calculus done. Result : " + result); }
	 */

	/**
	 * @param readObject
	 *            2
	 */
	public static void treatResult(Task readObject) {
		TestTaskWithParameters temp = (TestTaskWithParameters) readObject;
		result = (result * (long) temp.result) % p;
		System.out.println("External intermediate calculus done. Result : "
				+ temp.result);
		System.out
				.println("External intermediate calculus done. Resultat en cours : "
						+ result);
	}
}