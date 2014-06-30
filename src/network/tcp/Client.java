package network.tcp;

import java.util.HashMap;
import network.NetworkClass;
import tasks.Task;

public class Client extends NetworkClass {

	private HashMap<String, Byte>	networkList;
	private String[]				classes;
	private Task					task;

	public Client(String[] c, Task t, HashMap<String, Byte> nL) {
		classes = c;
		task = t;
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
		for (String ip : networkList.keySet()) {
			System.out.println("Creating a TCPClient to: " + ip);
			TCPClient client = new TCPClient((short) 1337, ip, classes, task);
			client.start();
		}

		System.out.println("Finished!");
	}
}