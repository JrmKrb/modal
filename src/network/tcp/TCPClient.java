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
import network.NetworkInterface;
import tasks.Task;

public class TCPClient extends NetworkInterface {

	private static ByteBuffer	writeBuff;
	public SocketChannel		clientSocket;

	private InetSocketAddress	remoteIP;
	private short				taskID;

	private String[]			classes;
	private Task				result;

	public TCPClient(short taskID, InetSocketAddress remoteIP, String[] classes) {
		this.taskID = taskID;
		this.remoteIP = remoteIP;
		writeBuff = ByteBuffer.allocate(1024000);
		this.classes = classes;
	}

	public TCPClient(short taskID, String remoteIP, String[] classes) {
		this.taskID = taskID;
		this.remoteIP = new InetSocketAddress(remoteIP, PORT);
		writeBuff = ByteBuffer.allocate(1024000);
		this.classes = classes;
	}

	public TCPClient(short taskID, InetSocketAddress remoteIP, Task t) {
		this.taskID = taskID;
		this.remoteIP = remoteIP;
		result = t;
	}

	/**
	 * 
	 */
	public void run() {
		try {
			clientSocket = SocketChannel.open();
			clientSocket.bind(new InetSocketAddress(0));
			InetSocketAddress remote = new InetSocketAddress(remoteIP.getAddress(), PORT);
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress() + ":" + remote.getPort() + "\n");
			sendIntro();
			if (classes != null) {
				for (int i = 0; i < classes.length - 1; i++)
					sendClass(classes[i]);
				String taskClass = classes[classes.length - 1];
				sendTask(taskClass);
				waitForAck();
				try {
					sendSerializedTask(TCPClient.class.getClassLoader().loadClass(taskClass).newInstance());
					waitForAck();
				}
				catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					System.out.println("Error : Serialized Task sending : " + e.getMessage());
				}
				askForExecution();
				waitForAck();
			} else {
				sendResult();
				waitForAck();
			}
		}
		catch (IOException e) {
			System.out.println("Error : TCPClient Constructor");
			e.printStackTrace();
		}
	}

	private void waitForAck() {
		DataInputStream dis;
		try {
			dis = new DataInputStream(clientSocket.socket().getInputStream());
			int type = dis.read();
			if (type != ACK) System.out.println("Received packet but not ACK packet.");
			int task = dis.readShort();
			System.out.println("Received ACK packet for task n°" + task + ".");
			long messLength = dis.readLong();
			byte[] message = new byte[(int) messLength];
			dis.read(message);
		}
		catch (IOException e) {
			System.out.println("Error while waiting for ACK packet : " + e.getMessage());
		}

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
			System.out.println("Buff sent");
			writeBuff.clear();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
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
	 * Dependancies
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
			e.printStackTrace();
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
			e.printStackTrace();
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