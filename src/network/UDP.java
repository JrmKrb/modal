package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class UDP {
	private HashMap<InetSocketAddress, Integer> networkList = new HashMap<InetSocketAddress, Integer>();
	private Thread listenerThread;
	private static DatagramChannel hostSocket;

	public UDP() {
		try {
			hostSocket = DatagramChannel.open();
			hostSocket.bind(new InetSocketAddress(12357));
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
			hostSocket.send(buff, new InetSocketAddress("255.255.255.255",
					12357));
		} catch (IOException e) {
			System.out.println("ERREUR PINGER");
			e.printStackTrace();
		}
	}

	/*
	 * Answer to a ping request
	 */
	public static void pingAnswer(InetSocketAddress remote) {
		try {
			// 0 si libre, 1 si busy TODO : Busy
			ByteBuffer buff = ByteBuffer.allocate(3);
			buff.put((byte) 1);
			buff.putShort((short) 0);
			buff.flip();
			hostSocket.send(buff, remote);
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
			ByteBuffer buff = ByteBuffer.allocate(256);
			while (true) {
				try {
					InetSocketAddress remote = (InetSocketAddress) hostSocket
							.receive(buff);
					byte[] tab = new byte[buff.position()];
					buff.flip();
					buff.get(tab);
					String received = new String(tab, StandardCharsets.UTF_16BE);
					buff.rewind();
					if (received.startsWith("WHOISONLINE")) {
						System.out.println("WHOISONLINE RECU");
						pingAnswer(remote);
					} else {
						int busy = buff.get();
						int numberTasks = buff.getShort();
						int[] tasks = new int[numberTasks];
						for (int i = 0; i < numberTasks; i++) {
							tasks[i] = buff.getShort(3 + 2 * i);
						}
						networkList.put(remote, busy);
						System.out.println("RECEIVED ANSWERS FROM PING " + busy
								+ " " + numberTasks);
					}
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
