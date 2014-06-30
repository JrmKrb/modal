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
import network.NetworkInterface;

public class UDPConsumer extends NetworkInterface {

	private HashMap<String, Byte>	networkList	= new HashMap<String, Byte>();
	private DatagramChannel				hostSocket;
	private InetSocketAddress			serverISA;
	private byte state = FREE;

	public UDPConsumer() {
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public UDPConsumer(InetSocketAddress isa) {
		serverISA = isa;
	}

	/**
	 * 
	 */
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
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			System.out.println("Error : UDP server");
			// e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void whoIsOnline() {
		try {
			ByteBuffer writeBuff = ByteBuffer.allocate(22);
			Util.bufferFromString(writeBuff, "WHOISONLINE");
			writeBuff.flip();
			hostSocket.send(writeBuff, new InetSocketAddress("192.168.0.199", PORT));
			System.out.println("whoIsOnline sent with "+hostSocket.getLocalAddress());
		}
		catch (IOException e) {
			System.out.println("Error : Whoisonline");
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	/**
	 * @return
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
