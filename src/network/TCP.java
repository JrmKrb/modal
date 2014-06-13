package network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCP {
	public ServerSocketChannel serverSocket;
	public SocketChannel clientSocket;
	private final static int PORT = 12347;

	private static ByteBuffer readBuff;
	private static ByteBuffer writeBuff;

	private final static byte INTRO = 0;
	private final static byte SIMPLECLASS = 1;
	private final static byte TASKCLASS = 2;
	private final static byte ACK = 3;
	private final static byte SERIALIZEDTASK = 4;
	private final static byte EXEC = 5;
	private final static byte EXECERROR = 6;
	private final static byte SERIALIZEDRESULT = 7;
	private final static byte END = 8;

	public TCP(String ip, int port) {
		// TODO: use getSendBufferSize and getReceiveBufferSize instead, see
		// connectClient
		// writeBuff = ByteBuffer.allocate(512000000);
		// readBuff = ByteBuffer.allocate(512000000);
		// connectClient(ip, port);
	}

	/**
	 * For client: connect to server
	 * 
	 * @param ip
	 * @param port
	 */
	public void connectClient(String ip, int port) {
		try {
			InetSocketAddress dest = new InetSocketAddress(ip, port);
			clientSocket = SocketChannel.open();
			clientSocket.connect(dest);
			writeBuff = ByteBuffer.allocate(1024000);
			readBuff = ByteBuffer.allocate(1024000);
		} catch (IOException e) {
			System.out.println("ERREUR connectClient : " + ip + ":" + port);
			e.printStackTrace();
		}
	}

	/**
	 * For server Listens on the port to create a new connection to a client who
	 * has a task to share.
	 */
	public void listen() {
		try {
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			InetSocketAddress local = new InetSocketAddress(PORT);
			serverSocket.bind(local);
			this.clientSocket = serverSocket.accept();
		} catch (IOException e) {
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
			clientSocket.write(writeBuff);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void introduction() {
		writeBuff.put(INTRO);
		// TODO TASKID writeBuff.putShort();
	}

	/**
	 * For Client
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void sendClass(String path) {
		writeBuff.put(SIMPLECLASS);
		// TODO TASKID writeBuff.putShort();
		writeBuff = Message.bufferFromClass(path, writeBuff);
	}

	/**
	 * For Client
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void sendTask(String path) {
		writeBuff.put(TASKCLASS);
		// TODO TASKID writeBuff.putShort();
		writeBuff = Message.bufferFromClass(path, writeBuff);
	}

	/**
	 * For Client & Servers
	 * 
	 * @return
	 */
	public void ack() {
		writeBuff.put(ACK);
		// TODO TASKID writeBuff.putShort();
	}

	public void serializedTask() {
		writeBuff.put(SERIALIZEDTASK);
		// TODO TASKID writeBuff.putShort();
	}

	// TODO
	public void execute() {
		return;
	}

	/**
	 * 
	 * @param error
	 */
	public void error(String error) {
		writeBuff.put(EXECERROR);
		// TODO TASKID writeBuff.putShort();
		writeBuff.putLong(error.length() * 2);
		Message.bufferFromString(writeBuff, error);
	}

	// TODO
	public void result() {
		return;
	}

	// TODO
	public void end() {
		return;
	}
}