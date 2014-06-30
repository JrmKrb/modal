package network.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import lib.Util;
import network.NetworkClass;

public class UDPConsumer extends NetworkClass {

	private HashMap<String, Byte>	networkList	= new HashMap<String, Byte>();
	private DatagramChannel				hostSocket;
	private InetSocketAddress			serverISA;
	private byte state = FREE;

	public UDPConsumer() {
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
		}
		catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	public UDPConsumer(InetSocketAddress isa) {
		serverISA = isa;
	}

	@Override
	public void run() {
		try {
			System.out.println("AAA");
			hostSocket = DatagramChannel.open();
			hostSocket.socket().setBroadcast(true);
			System.out.println("BBB");
			hostSocket.bind(serverISA);
			System.out.println("UDP Consumer listening on " + hostSocket.getLocalAddress());
			whoIsOnline();
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
						byte busy = buff.get();
						networkList.put(remote.getAddress().getHostAddress(), busy);
						int nbTasks = buff.getShort();
						short[] tasks = new short[nbTasks];
						for (int i = 0; i < nbTasks; i++)
							tasks[i] = buff.getShort();
						System.out.println("Answer received from " + remote + " : " + busy);
					}
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void whoIsOnline() {
		try {
			ByteBuffer writeBuff = ByteBuffer.allocate(22);
			Util.bufferFromString(writeBuff, "WHOISONLINE");
			writeBuff.flip();
			hostSocket.send(writeBuff, new InetSocketAddress("255.255.255.255", PORT));
			System.out.println("whoIsOnline sent with "+hostSocket.getLocalAddress());
		}
		catch (IOException e) {
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
		}
		catch (IOException e) {
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
