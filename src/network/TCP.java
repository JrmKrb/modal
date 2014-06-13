package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCP {
	private final static byte INTRO = 0;
	private final static byte SIMPLECLASS = 1;
	private final static byte TASKCLASS = 2;
	private final static byte ACK = 3;
	private final static byte SERIALIZEDTASK = 4;
	private final static byte EXEC = 5;
	private final static byte EXECERROR = 6;
	private final static byte RESULT = 7;
	private final static byte END = 8;

	public ServerSocketChannel serverSocket;
	public SocketChannel clientSocket;

	private final static int PORT = 12347;
	private final static int TIMEOUTLENGTH = 4;
	private final static int TIMEOUT = 30;

	private static ByteBuffer readBuff;
	private static ByteBuffer writeBuff;

	private short taskID;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param taskID
	 */
	public TCP(String ip, int port, short taskID) {
		// connectClient
		// writeBuff = ByteBuffer.allocate(512000000);
		// readBuff = ByteBuffer.allocate(512000000);
		// connectClient(ip, port);
		this.taskID = taskID;
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

	// TODO : Faire la fonction de listening
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

	/**
	 * Début connection TCP
	 */
	public void introduction() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
	}

	/**
	 * 
	 * @param path
	 */
	public void sendClass(String path) {
		writeBuff.put(SIMPLECLASS);
		writeBuff.putShort(taskID);
		writeBuff = Message.bufferFromClass(path, writeBuff);
	}

	/**
	 * Envoi Tâche
	 * 
	 * @param path
	 */
	public void sendTask(String path) {
		writeBuff.put(TASKCLASS);
		writeBuff.putShort(taskID);
		writeBuff = Message.bufferFromClass(path, writeBuff);
	}

	/**
	 * Envoi Acknowledgment
	 */
	public void ack() {
		writeBuff.put(ACK);
		writeBuff.putShort(taskID);
	}

	/**
	 * Envoi tâche sérialisée (avec données)
	 */
	public void serializedTask() {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
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
		writeBuff.putLong(error.length() * 2);
		Message.bufferFromString(writeBuff, error);
	}

	/**
	 * Envoi du résultat
	 * 
	 * @param obj
	 */
	public void result(Serializable obj) {
		try {
			writeBuff.put(RESULT);
			writeBuff.putShort(taskID);
			byte[] serializedObj = serialize(obj);
			// TODO : Vérifier que le byte[] est de la bonne longueur et qu'il
			// n'y a pas du contenu vide à la fin
			writeBuff.putLong(serializedObj.length);
			sendBuff();
			clientSocket.socket().getOutputStream().write(serializedObj);
		} catch (IOException e) {
			System.out.println("ERREUR ENVOI RESULTAT SERIALISE");
			e.printStackTrace();
		}
	}

	/**
	 * Fin de connection
	 */
	public void end() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
	}

	// TODO : Outils de désérialisation dans un autre fichier

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