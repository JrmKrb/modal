package network.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

import lib.Util;
import network.NetworkClass;

public class UDPConsumer extends NetworkClass {

	private HashMap<String, Byte> networkList = new HashMap<String, Byte>();
	private DatagramChannel hostSocket;
	private InetSocketAddress serverISA;
	private byte state = FREE;
	private LinkedList<String> suppliersIP;

	public UDPConsumer() {
		suppliersIP = new LinkedList<String>();
		suppliersIP.add("255.255.255.255");
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost()
					.getHostAddress(), PORT);
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	public UDPConsumer(LinkedList<String> ips) {
		suppliersIP = ips;
		suppliersIP.add("255.255.255.255");
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost()
					.getHostAddress(), PORT);
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	public UDPConsumer(InetSocketAddress isa) {
		serverISA = isa;
	}

	@Override
	public void run() {
		try {
			hostSocket = DatagramChannel.open();
			hostSocket.socket().setBroadcast(true);
			hostSocket.bind(serverISA);
			System.out.println("UDP Consumer listening on "
					+ hostSocket.getLocalAddress());
			whoIsOnline();
			ByteBuffer buff = ByteBuffer.allocate(22);
			while (true) {
				try {
					InetSocketAddress remote = (InetSocketAddress) hostSocket
							.receive(buff);
					byte[] tab = new byte[buff.position()];
					buff.flip();
					buff.get(tab);
					String received = new String(tab, StandardCharsets.UTF_16BE);
					buff.rewind();
					if (!received.startsWith("WHOISONLINE")) {
						byte busy = buff.get();
						networkList.put(remote.getAddress().getHostAddress(),
								busy);
						int nbTasks = buff.getShort();
						short[] tasks = new short[nbTasks];
						for (int i = 0; i < nbTasks; i++)
							tasks[i] = buff.getShort();
						System.out.println("Answer received from " + remote
								+ " : " + busy);
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void whoIsOnline() {
		try {
			ByteBuffer writeBuff = ByteBuffer.allocate(22);
			for (String supplierIP : suppliersIP) {
				Util.bufferFromString(writeBuff, "WHOISONLINE");
				writeBuff.flip();
				hostSocket.send(writeBuff, new InetSocketAddress(supplierIP,
						PORT));
				System.out.println("whoIsOnline sent to "
						+ supplierIP+"/"+PORT);
				writeBuff.clear();
			}
		} catch (IOException e) {
			System.out.println("Error Whoisonline: " + e.getMessage());
		}
	}

	/**
	 * Answer to a ping request
	 * 
	 * @param remote
	 */
	public void pingAnswer(InetSocketAddress remote) {
		try {
			ByteBuffer buff = ByteBuffer.allocate(3);
			buff.put(state);
			buff.putShort((short) 0);
			buff.flip();
			hostSocket.send(buff, remote);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @return the networkList
	 */
	public HashMap<String, Byte> getNetworkList() {
		return networkList;
	}

	/**
	 * @param b
	 */
	public void setState(byte b) {
		state = b;
	}
}
