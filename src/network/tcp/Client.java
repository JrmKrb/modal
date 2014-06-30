package network.tcp;

import java.util.HashMap;
import network.NetworkInterface;
import tasks.Task;

public class Client extends NetworkInterface {

	private HashMap<String, Byte>	networkList;
	private String[]				classes;
	private Task					toDo;

	public Client(String[] c, Task t, HashMap<String, Byte> nL) {
		classes = c;
		toDo = t;
		networkList = nL;
	}

	/**
	 */
	public void run() {

		for (String ip : networkList.keySet()) {
			System.out.println("Ouverture d'un TCPClient vers : " + ip);
			TCPClient client = new TCPClient((short) 1337, ip, classes, toDo);
			client.start();
		}

		System.out.println("Finished !");
	}
}