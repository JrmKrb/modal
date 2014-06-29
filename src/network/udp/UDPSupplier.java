package network.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import network.NetworkInterface;

public class UDPSupplier extends NetworkInterface {

	private DatagramChannel		hostSocket;
	private InetSocketAddress	serverISA;

	public UDPSupplier() {
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public UDPSupplier(InetSocketAddress isa) {
		serverISA = isa;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			hostSocket = DatagramChannel.open();
			hostSocket.bind(serverISA);
			System.out.println("UDP Supplier listening on : " + hostSocket.getLocalAddress());
			ByteBuffer buff = ByteBuffer.allocate(22);
			while (true) {
				try {
					InetSocketAddress remote = (InetSocketAddress) hostSocket.receive(buff);
					byte[] tab = new byte[buff.position()];
					buff.flip();
					buff.get(tab);
					String received = new String(tab, StandardCharsets.UTF_16BE);
					buff.rewind();
					if (received.startsWith("WHOISONLINE")) {
						System.out.println("WHOISONLINE received by : " + remote);
						pingAnswer(remote);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			System.out.println("Error : UDP Supplier.");
			// e.printStackTrace();
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
			buff.put((byte) FREE);
			buff.putShort((short) 0);
			buff.flip();
			hostSocket.send(buff, remote);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
