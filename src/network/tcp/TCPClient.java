package network.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import network.NetworkClass;
import tasks.Task;

public class TCPClient extends NetworkClass {

	public SocketChannel		clientSocket;

	private InetSocketAddress	remoteIP;
	private short taskID;
	MessageSender m;

	private String[]			classes;
	private Task				instanceOfTask;
	private Task				result;

	/**
	 * @param taskID
	 * @param remoteIP
	 * @param classes
	 * @param t
	 */
	public TCPClient(short taskID, InetSocketAddress remoteIP, String[] classes, Task t) {
		this.taskID = taskID;
		this.remoteIP = remoteIP;
		this.classes = classes;
		instanceOfTask = t;
	}

	/**
	 * @param taskID
	 * @param remoteIP
	 * @param classes
	 * @param t
	 */
	public TCPClient(short taskID, String remoteIP, String[] classes, Task t) {
		this.taskID = taskID;
		this.remoteIP = new InetSocketAddress(remoteIP, PORT);
		this.classes = classes;
		instanceOfTask = t;
	}

	/**
	 * @param taskID
	 * @param remoteIP
	 * @param t
	 */
	public TCPClient(short taskID, InetSocketAddress remoteIP, Task t) {
		this.taskID = taskID;
		this.remoteIP = remoteIP;
		result = t;
	}

	@Override
	public void run() {
		try {
			clientSocket = SocketChannel.open();
			clientSocket.bind(new InetSocketAddress(0));
			InetSocketAddress remote = new InetSocketAddress(remoteIP.getAddress(), PORT);
			m = new MessageSender(ByteBuffer.allocate(1024000), taskID, clientSocket);
			System.out.println("Trying to connect to " + remote + ".");
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress() + ":" + remote.getPort() + "\n");
			m.sendIntro();
			waitForAck();
			System.out.println("Intro sent");
			if (classes != null) {
				for (int i = 0; i < classes.length - 1; i++) {
					m.sendClass(classes[i]);
					System.out.println("Class "+classes[i]+" sent.");
				}
				String taskClass = classes[classes.length - 1];
				m.sendTask(taskClass);
				System.out.println("Task sent");
				waitForAck();
				m.sendSerializedTask(instanceOfTask);
				System.out.println("Serialized Task sent");
				waitForAck();
				m.askForExecution();
				System.out.println("Execution asked");
				waitForAck();
			} else {
				m.sendResult(result);
				System.out.println("Result sent");
				waitForAck();
			}
		}
		catch (IOException e) {
			System.out.println("Error: TCPClient");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private void waitForAck() throws IOException {
		System.out.println("Waiting for ACK");
		DataInputStream dis;
			dis = new DataInputStream(clientSocket.socket().getInputStream());
			int type = dis.read();
			if (type != ACK) {
				System.out.println("Received packet but not ACK packet.");
				throw new IOException("Packet is not a ACK packet");
			}
			int task = dis.readShort();
			System.out.println("Received ACK packet for task n°" + task);
			long messLength = dis.readLong();
			byte[] message = new byte[(int) messLength];
			dis.read(message);
	}

}