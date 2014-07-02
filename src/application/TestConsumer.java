package application;

import java.util.LinkedList;

import network.tcp.Client;
import network.tcp.Server;
import network.udp.UDPConsumer;
import tasks.MergeSortCalculus;
import tasks.Task;
import tasks.TestTaskMerge;

public class TestConsumer {

	static long result = 1L;
	static int p = 104729;
	static int count = 0;
	static int n = 10;
	static int[] mergeRes = new int[n];

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

//		String[] classes = { "bin/tasks/TestTaskWithParameters.class" };
//		Task[] tasks = { new TestTaskWithParameters(1, 5, p),
//				new TestTaskWithParameters(6, 100000, p) };
//
//		Client TCPClient = new Client(classes, tasks, udp.getNetworkList());
//
//		System.out.println("Everything is launched.");
//
//		long res2 = 1;
//		for (int k = 1; k <= 100000; k++) {
//			res2 = (res2 * k) % p;
//		}
//		System.out.println("Resultat local : " + res2);

		/*
		 * TEST 3 MERGE SORT
		 */
		
		 int[] t = randomTab(n); for(int i=0;i<n;i++)
		 System.out.print(t[i]+" "); System.out.println();
		 
		 String[] classes = { "bin/tasks/MergeSortCalculus.class" ,
		 "bin/tasks/TestTaskMerge.class" }; Task[] tasks = { new
		 TestTaskMerge(0, n/2, t), new TestTaskMerge(n/2+1, n-1, t) }; Client
		 TCPClient = new Client(classes, tasks, udp.getNetworkList());
		 
new MergeSortCalculus(t).compute();
		 
		Thread.sleep(1000);
		TCPClient.start();

		System.out.println("Everything is launched.");
	}

	/**
	 * @param readObject
	 * 
	 */

	
//	 public static void treatResult(Task readObject) { TestTask temp =
//	 (TestTask) readObject; result = temp.result;
//	 System.out.println("External calculus done. Result : " + result); }
	 

	/**
	 * @param readObject
	 * 
	 */
//	public static void treatResult(Task readObject) {
//		TestTaskWithParameters temp = (TestTaskWithParameters) readObject;
//		result = (result * (long) temp.result) % p;
//		System.out.println("External intermediate calculus done. Result : "
//				+ temp.result);
//		System.out
//				.println("External intermediate calculus done. Resultat en cours : "
//						+ result);
//	}

	/**
	 * @param readObject
	 *            MERGE SORT
	 * 
	 */
	
	 public static void treatResult(Task readObject) { TestTaskMerge temp =
	 (TestTaskMerge) readObject;
	 System.out.println("External intermediate calculus done."); if (count ==
	 0) { for (int i=0;i<n/2;i++) mergeRes[i]=temp.ms.tab[i]; count++; } else
	 if (count == 1) { for (int i=n/2+1;i<n;i++) mergeRes[i]=temp.ms.tab[i];
	 System.out.println("FIN DU CALCUL :"); printMergeRes(); } }
	 
	public static void printMergeRes() {
		System.out.print(mergeRes[0]);
		for (int i = 1; i < n; i++)
			System.out.print(" " + mergeRes[i]);
		System.out.println();
	}

	public static int[] randomTab(int n) {
		int[] res = new int[n];
		for (int i = 0; i < n; i++)
			res[i] = (int) (20 * n * (Math.random() - 0.5));
		return res;
	}

}