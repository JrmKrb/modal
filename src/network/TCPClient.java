package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import tasks.Task;

public class TCPClient extends NetworkInterface {

	private static ByteBuffer	writeBuff;
	public SocketChannel		clientSocket;

	private InetSocketAddress				remoteIP;
	private short				taskID;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param taskID
	 */
	public TCPClient(short taskID, InetSocketAddress remoteIP) {
		this.taskID = taskID;
		this.remoteIP = remoteIP;
		writeBuff = ByteBuffer.allocate(1024000);
	}

	public void run() {
		try {
			clientSocket = SocketChannel.open();
			clientSocket.bind(new InetSocketAddress(0));
			InetSocketAddress remote = new InetSocketAddress(remoteIP.getAddress(), 12348);
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress() + ":" + remote.getPort() + "\n");
			while (true)
				;
		}
		catch (IOException e) {
			System.out.println("Error : TCPClient Constructor");
			e.printStackTrace();
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
	public void intro() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
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
	public void sendSerializedTask(Task t) throws IOException {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(t);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), t);
	}

	/**
	 * Asking for execution
	 */
	public void execute() {
		writeBuff.put(EXEC);
		writeBuff.putShort(taskID);
		writeBuff.putLong(TIMEOUTLENGTH);
		writeBuff.putInt(TIMEOUT);
	}

	/**
	 * If error
	 * 
	 * @param error
	 */
	public void error(String error) {
		writeBuff.put(EXECERROR);
		writeBuff.putShort(taskID);
		Long n = (long) error.length() * 2;
		writeBuff.putLong(n);
		Util.bufferFromString(writeBuff, error);
	}

	/**
	 * Sending result
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public void result(Serializable obj) throws IOException {
		writeBuff.put(RESULT);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(obj);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), obj);
	}

	/**
	 * Asking for end of connection
	 */
	public void end() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
	}

}