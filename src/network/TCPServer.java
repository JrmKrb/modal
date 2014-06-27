package network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import application.sumTask;

public class TCPServer extends Thread {
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

	public ServerSocketChannel serverSocket;
	public SocketChannel clientSocket;

	// TODO : Finir le Thread listener et faire le classLoader

	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", PORT));
			System.out.println("Serveur en attente d'un client.");
			clientSocket = serverSocket.accept();
			System.out.println("Serveur en attente d'un message.");
			DataInputStream dis = new DataInputStream(clientSocket.socket()
					.getInputStream());
			while (true) {
				System.out.println("Waiting for Type");
				byte tempType = dis.readByte();
				System.out.println("Waiting for taskID");
				short tempTaskID = dis.readShort();
				System.out.println("taskID : " + tempTaskID + "\n");
				int messLength = 0;
				switch (tempType) {
				case INTRO:
					System.out.println("READING INTRO PACKET");
					break;
				case SIMPLECLASS:
					System.out.println("READING SIMPLECLASS PACKET");
					messLength = (int) dis.readLong();
					// TODO get Class
					break;
				case TASKCLASS:
					System.out.println("READING TASKCLASS PACKET");
					messLength = (int) dis.readLong();
					// TODO get Task
					break;
				case ACK:
					System.out.println("READING ACK PACKET");
					break;
				case SERIALIZEDTASK:
					System.out.println("READING SERIALIZEDTASK PACKET");
					System.out.println(messLength = (int) dis.readLong());
					// TODO get SerTask
					break;
				case EXEC:
					System.out.println("READING EXEC PACKET");
					messLength = (int) dis.readLong();
					long timeout;
					System.out
							.println("Timeout : " + (timeout = dis.readInt()));
					// TODO Indiquer au thread d'exécuter le code
					break;
				case EXECERROR:
					System.out.println("READING EXECERROR PACKET");
					messLength = (int) dis.readLong();
					for (int i = 0; i < messLength; i++)
						System.err.print(dis.readByte());
					break;
				case RESULT:
					System.out.println("READING RESULT PACKET");
					messLength = (int) dis.readLong();
					Object o = Message.getObject(clientSocket.socket());
					sumTask st = (sumTask) o;
					System.out.println("RESULT : "+st.result);
					// TODO get Result
					break;
				case END:
					System.out.println("READING END CONNECTION PACKET");
					//TODO endConnection
					break;
				}
				System.out.println("Taille message : " + messLength);
				// On lit le message :

				byte[] message = new byte[(int) messLength];
				System.out.println(new String(message, "UTF-16BE")
						+ "\nPACKET RECEIVED => Go to next one\n\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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