package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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

	private final static int PORT = 12347;
	private final static int TIMEOUTLENGTH = 4;
	private final static int TIMEOUT = 30;

	private static ByteBuffer readBuff;
	private static ByteBuffer writeBuff;
	public ServerSocketChannel serverSocket;
	public SocketChannel clientSocket;

	private short taskID;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param taskID
	 */
	public TCP(String ip, int port, short taskID) {
		writeBuff = ByteBuffer.allocate(1024000);
		readBuff = ByteBuffer.allocate(1024000);
		connectClient(ip, port);
		this.taskID = taskID;
		listener.start();
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 */
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

	// RMI Remote Message Invocation

	// TODO : Faire le Thread listener et le classLoader
	/**
	 * Thread Listener
	 */
	public Thread listener = new Thread() {
		public void run() {
			try {
				DataInputStream dis = new DataInputStream(clientSocket.socket()
						.getInputStream());
				while (true) {
					byte tempType = dis.readByte();
					short tempTaskID = dis.readShort();
					long messLength;
					switch (tempType) {
					case INTRO:
						System.out.println("INTRO PACKET RECEIVED");
						break;
					case SIMPLECLASS:
						System.out.println("READING SIMPLECLASS PACKET");
						messLength = dis.readLong();
						// TODO get Class
						break;
					case TASKCLASS:
						System.out.println("READING TASKCLASS PACKET");
						messLength = dis.readLong();
						// TODO get Task
						break;
					case ACK:
						System.out.println("READING ACK PACKET");
						break;
					case SERIALIZEDTASK:
						System.out.println("READING SERIALIZEDTASK PACKET");
						messLength = dis.readLong();
						// TODO get SerTask
						break;
					case EXEC:
						System.out.println("READING EXEC PACKET");
						messLength = dis.readLong();
						long timeout = dis.readInt();
						// TODO Indiquer au thread d'exécuter le tout
						break;
					case EXECERROR:
						System.out.println("READING EXECERROR PACKET");
						messLength = dis.readLong();
						// TODO get message d'erreur
						break;
					case RESULT:
						System.out.println("READING RESULT PACKET");
						messLength = dis.readLong();
						// TODO get Result
						break;
					case END:
						System.out.println("FIN CONNECTION");
						// TODO : stoper la connection ?
						break;
					}
				}
			} catch (IOException e) {
				// TODO Bloc catch généré automatiquement
				e.printStackTrace();
			}
		}
	};

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
	 * Envoi classes annexes
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