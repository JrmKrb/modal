package network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCP {
	public ServerSocketChannel serverSocket;
	public SocketChannel clientSocket;
	private final static int PORT = 12347;
	private final static int INTRO = 0;
	private final static int SIMPLECLASS = 1;
	private final static int TASKCLASS = 2;
	private final static int ACK = 3;
	private final static int SERIALIZEDTASK = 4;
	private final static int EXEC = 5;
	private final static int EXECERROR = 6;
	private final static int SERIALIZEDRESULT = 7;
	private final static int END = 8;

	public TCP(String ip, int port) {
		// connectClient(ip, port);
	}

	// For client: connect to server
	public void connectClient(String ip, int port) {
		try {
			InetSocketAddress dest = new InetSocketAddress(ip, port);
			clientSocket = SocketChannel.open();
			clientSocket.connect(dest);
		} catch (IOException e) {
			System.out.println("ERREUR connectClient : " + ip + ":" + port);
			e.printStackTrace();
		}
	}

	// For Client
	public void sendClass(String path) throws IOException {
		FileChannel fisC;
		try {
			ByteBuffer buff = Message.bufferFromClass(path);
			clientSocket.write(buff);
		} catch (FileNotFoundException e) {
			System.out.println("Ficher " + path + "non trouvé !");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sendMessage(ByteBuffer message) {
		try {
			clientSocket.write(message);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// TODO
	public boolean introduction() {
		return true;
	}

	// TODO
	// For Client & Servers
	public boolean ack() {
		return true;
	}

	// TODO
	public boolean serializedTask() {
		return true;
	}

	// TODO
	public boolean execute() {
		return true;
	}

	// TODO
	public boolean error() {
		return false;
	}

	// TODO
	public boolean result() {
		return false;
	}

	// TODO
	public boolean end() {
		return false;
	}

	// TODO
	public void sendResult() {
	}
}