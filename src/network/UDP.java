package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Scanner;

public class UDP {
	private HashMap<InetSocketAddress, Integer> networkList = new HashMap<InetSocketAddress, Integer>();
	private Thread listenerThread;
	private DatagramChannel serverSocket;

	public UDP() {
		try {
			serverSocket = DatagramChannel.open();
			serverSocket.bind(new InetSocketAddress(12357));
		} catch (IOException e) {
			System.out.println("ERREUR PINGER");
			e.printStackTrace();
		}
		listenerThread = listener;
		listenerThread.start();
		System.out.println("ListenerThread appelé");
	}

	public void whoIsOnline() {
		try {
			ByteBuffer buff = Message.bufferFromString("WHOISONLINE");
			buff.flip();
			serverSocket.send(buff,new InetSocketAddress("255.255.255.255",12357));
			buff.clear();
			System.out.println(buff.asCharBuffer().toString()+" sent to 255.255.255.255/12357.");
		} catch (IOException e) {
			System.out.println("ERREUR PINGER");
			e.printStackTrace();
		}
	}

	/*
	 * Answer to a ping request
	 */
	public static void pingAnswer(DatagramChannel servSocket,
			InetSocketAddress remote) {
		try {
			// 0 si libre, 1 si busy TODO : Busy
			ByteBuffer buff = ByteBuffer.allocate(100);
			buff.put((byte) 1);
			buff.putShort(1, (short) 0);
			buff.flip();
			servSocket.send(buff, remote);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Le Listener attend les requête Ping et les réponses aux ping
	 */
	private Thread listener = new Thread() {
		public void run() {
			System.out.println("LISTENER EN ATTENTE");
			ByteBuffer buff = ByteBuffer.allocate(256000);
			while (true) {
				try {
					InetSocketAddress remote = (InetSocketAddress) serverSocket
							.receive(buff);
					String receivedString = buff.asCharBuffer().toString();
					System.out.println("Received FROM " + remote + " : "
							+ receivedString);
					int busy = buff.get();
					int numberTasks = buff.getShort(1);
					int[] tasks = new int[numberTasks];
					for (int i = 0; i < numberTasks; i++) {
						tasks[i] = buff.getShort(3 + 2 * i);
					}
					networkList.put(remote, busy);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/*
	 * Clients need this to dispatch the work
	 */
	public HashMap<InetSocketAddress, Integer> getNetworkList() {
		return networkList;
	}
}
