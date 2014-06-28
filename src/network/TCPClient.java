package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;

public class TCPClient extends Thread {
	private final static byte INTRO = 0;
	private final static byte SIMPLECLASS = 1;
	private final static byte TASKCLASS = 2;
	private final static byte ACK = 3;
	private final static byte SERIALIZEDTASK = 4;
	private final static byte EXEC = 5;
	private final static byte EXECERROR = 6;
	private final static byte RESULT = 7;
	private final static byte END = 8;

	private final static int PORT = 12347;
	private final static int TIMEOUTLENGTH = 4;
	private final static int TIMEOUT = 30;
	private final String serverIP;

	private static ByteBuffer writeBuff;
	public SocketChannel clientSocket;

	private short taskID;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param taskID
	 */
	public TCPClient(String ip, short taskID) {
		this.taskID = taskID;
		serverIP = ip;
		writeBuff = ByteBuffer.allocate(1024000);
	}

	public void run() {
		try {
			clientSocket = SocketChannel.open();
			clientSocket.bind(new InetSocketAddress(0));
			InetSocketAddress remote = new InetSocketAddress(serverIP, PORT);
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress()
					+ ":" + remote.getPort() + "\n");
		} catch (IOException e) {
			System.out.println("ERREUR CONSTRUCTEUR TCP CLIENT");
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
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Début connection TCP
	 */
	public void intro() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
	}

	/**
	 * Envoi classes annexes
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
		} catch (IOException e) {
			writeBuff.clear();
			e.printStackTrace();
		}
	}

	/**
	 * Envoi Tâche
	 * 
	 * @param path
	 */
	public void sendTask(String path) {
		FileChannel fc = Message.channelFromFile(path);
		writeBuff.put(TASKCLASS);
		writeBuff.putShort(taskID);
		try {
			writeBuff.putLong(fc.size());
			sendBuff();
			fc.transferTo(0, fc.size(), clientSocket);
		} catch (IOException e) {
			writeBuff.clear();
			e.printStackTrace();
		}
	}

	/**
	 * Envoi Acknowledgment
	 */
	public void ack() {
		writeBuff.put(ACK);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
	}

	/**
	 * Envoi tâche sérialisée (avec données)
	 */
	public void serializedTask() {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
		//TODO
	}

	/**
	 * Exécution effectuée
	 */
	public void execute() {
		writeBuff.put(EXEC);
		writeBuff.putShort(taskID);
		writeBuff.putLong(TIMEOUTLENGTH);
		writeBuff.putInt(TIMEOUT);
	}

	/**
	 * Erreur lors de l'exécution
	 * 
	 * @param error
	 */
	public void error(String error) {
		writeBuff.put(EXECERROR);
		writeBuff.putShort(taskID);
		//TODO: why this length?
		writeBuff.putLong(error.length() * 2);
		Message.bufferFromString(writeBuff, error);
	}

	/**
	 * Envoi du résultat
	 * 
	 * @param obj
	 */
	public void result(Serializable obj) {
		writeBuff.put(RESULT);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
		// TODO : envoyer la VRAIE taille de l'objet.
		sendBuff();
		Message.sendObject(clientSocket.socket(), obj);
	}

	/**
	 * Fin de connection
	 */
	public void end() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
	}

	// TODO : Outils de (dé)sérialisation dans un autre fichier

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static byte[] serialize(Serializable o) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			new ObjectOutputStream(bos).writeObject(o);
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println("ERREUR SERIALISATION");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param buff
	 * @return
	 */
	public static Serializable unSerialize(ByteBuffer buff) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(buff.array(),
					0, buff.limit());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (Serializable) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("ERREUR DESERIALISATION");
			e.printStackTrace();
		}
		return null;
	}

}