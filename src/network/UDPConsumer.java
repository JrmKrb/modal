package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class UDPConsumer extends NetworkInterface {

	private HashMap<InetSocketAddress, Integer>	networkList	= new HashMap<InetSocketAddress, Integer>();
	private static DatagramChannel				hostSocket;
	public final static int						FREE		= 0;
	public final static int						BUSY		= 1;

	public void run() {
		try {
			hostSocket = DatagramChannel.open();
			hostSocket.bind(new InetSocketAddress(PORT));
			System.out.println("UDP Consumer listening on "+hostSocket.getLocalAddress());
			ByteBuffer buff = ByteBuffer.allocate(22);
			while (true) {
				try {
					InetSocketAddress remote = (InetSocketAddress) hostSocket.receive(buff);
					byte[] tab = new byte[buff.position()];
					buff.flip();
					buff.get(tab);
					String received = new String(tab, StandardCharsets.UTF_16BE);
					buff.rewind();
					if (!received.startsWith("WHOISONLINE")) {
						int busy = buff.get();
						networkList.put(remote, busy);
						int nbTasks = buff.getShort();
						short[] tasks = new short[nbTasks];
						for (int i = 0; i < nbTasks; i++)
							tasks[i] = buff.getShort();
						System.out.println("Answer received from " + remote + " : " + busy);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			System.out.println("Error : UDP server");
			// e.printStackTrace();
		}
	}

	public void whoIsOnline() {
		try {
			ByteBuffer writeBuff = ByteBuffer.allocate(22);
			Util.bufferFromString(writeBuff, "WHOISONLINE");
			writeBuff.flip();
			hostSocket.send(writeBuff, new InetSocketAddress("255.255.255.255", 12347));
		}
		catch (IOException e) {
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
			// TODO taskID
			ByteBuffer buff = ByteBuffer.allocate(3);
			buff.put((byte) FREE);
			buff.putShort((short) 0);
			buff.flip();
			hostSocket.send(buff, remote);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Le Listener attend les requête Ping et les réponses aux ping
	 */
	private Thread	listener	= new Thread() {

									public void run() {
										System.out.println("LISTENER EN ATTENTE");
										ByteBuffer buff = ByteBuffer.allocate(256);
										while (true) {
											try {
												InetSocketAddress remote = (InetSocketAddress) hostSocket.receive(buff);
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
													System.out.println("RECEIVED ANSWERS FROM PING " + busy + " " + numberTasks);
												}
											}
											catch (IOException e) {
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
