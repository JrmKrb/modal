package network.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import lib.Util;
import network.NetworkClass;
import tasks.Task;

public class TCPClient extends NetworkClass {

	private static ByteBuffer	writeBuff;
	public SocketChannel		clientSocket;

	private InetSocketAddress	remoteIP;
	private short				taskID;

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
		writeBuff = ByteBuffer.allocate(1024000);
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
		writeBuff = ByteBuffer.allocate(1024000);
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
		writeBuff = ByteBuffer.allocate(1024000);
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
			System.out.println("Trying to connect to " + remote + ".");
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress() + ":" + remote.getPort() + "\n");
			sendIntro();
			waitForAck();
			System.out.println("Intro sent");
			if (classes != null) {
				for (int i = 0; i < classes.length - 1; i++) {
					sendClass(classes[i]);
					System.out.println("Class "+classes[i]+" sent.");
				}
				String taskClass = classes[classes.length - 1];
				sendTask(taskClass);
				System.out.println("Task sent");
				waitForAck();
				sendSerializedTask(instanceOfTask);
				System.out.println("Serialized Task sent");
				waitForAck();
				askForExecution();
				System.out.println("Execution asked");
				waitForAck();
			} else {
				sendResult();
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

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendBuff() {
		try {
			writeBuff.flip();
			clientSocket.write(writeBuff);
			writeBuff.clear();
			return true;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	 * First packet
	 */
	public void sendIntro() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
		sendBuff();
	}

	/**
	 * Dependencies
	 * 
	 * @param path
	 */
	public void sendClass(String path) {
		writeBuff.put(SIMPLECLASS);
		writeBuff.putShort(taskID);
		FileChannel fc;
		try {
			fc = FileChannel.open(Paths.get(path));
			writeBuff.putLong(fc.size());
			sendBuff();
			fc.transferTo(0, fc.size(), clientSocket);
		}
		catch (IOException e) {
			writeBuff.clear();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Task sender
	 * 
	 * @param path
	 */
	public void sendTask(String path) {
		FileChannel fc = Util.channelFromFile(path);
		writeBuff.put(TASKCLASS);
		writeBuff.putShort(taskID);
		try {
			writeBuff.putLong(fc.size());
			sendBuff();
			fc.transferTo(0, fc.size(), clientSocket);
		}
		catch (IOException e) {
			writeBuff.clear();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Serialized Task Sender
	 * 
	 * @throws IOException
	 */
	public void sendSerializedTask(Object t) {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut;
		try {
			oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(t);
			oOut.close();
		}
		catch (IOException e) {
			System.out.println("Error : Serialized Task.");
		}
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), t);
		sendBuff();
	}

	/**
	 * Asking for execution
	 */
	public void askForExecution() {
		writeBuff.put(EXEC);
		writeBuff.putShort(taskID);
		writeBuff.putLong(TIMEOUTLENGTH);
		writeBuff.putInt(TIMEOUT);
		sendBuff();
	}

	/**
	 * If error
	 * 
	 * @param error
	 */
	public void sendError(String error) {
		writeBuff.put(EXECERROR);
		writeBuff.putShort(taskID);
		Long n = (long) error.length() * 2;
		writeBuff.putLong(n);
		Util.bufferFromString(writeBuff, error);
		sendBuff();
	}

	/**
	 * Sending result
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public void sendResult() throws IOException {
		writeBuff.put(RESULT);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(result);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), result);
	}

	/**
	 * Asking for end of connection
	 */
	public void askForEnd() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
		sendBuff();
	}

}