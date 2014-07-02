package network.tcp;

import java.util.HashMap;
import network.NetworkClass;
import tasks.Task;

public class Client extends NetworkClass {

	private HashMap<String, Byte>	networkList;
	private String[]				classes;
	private Task[]					tasks;
	private short taskID = 1337;

	public Client(String[] c, Task[] t, HashMap<String, Byte> nL) {
		classes = c;
		tasks = t;
		networkList = nL;
	}

	@Override
	public void run() {

		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
		
		int count = 0;
		for (String ip : networkList.keySet()) {
			System.out.println("Creating a TCPClient to: " + ip);
			TCPClient client = new TCPClient(taskID++, ip, classes, tasks[count++]);
			client.start();
		}

		System.out.println("Finished!");
	}
}