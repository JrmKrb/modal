package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import application.Task;
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

	private final static int PORT = 12357;

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
			Task serializedTask = null;
			while (true) {
				System.out.println("Waiting for Type");
				byte tempType = dis.readByte();
				System.out.println("Waiting for taskID");
				short tempTaskID = dis.readShort();
				System.out.println("taskID : " + tempTaskID + "\n");
				long messLength = dis.readLong();
				System.out.println("Taille message : " + messLength);
				NetworkClassLoader classLoader = new NetworkClassLoader(TCPServer.class.getClassLoader());
				switch (tempType) {
				case INTRO:
					System.out.println("READING INTRO PACKET");
					break;
				case SIMPLECLASS:
					System.out.println("READING SIMPLECLASS PACKET");

					Class<?> simpleClass = Message.getClass(
							clientSocket.socket(), (int) messLength);
					classLoader.loadClass(simpleClass.getName());
					break;
				case TASKCLASS:
					System.out.println("READING TASKCLASS PACKET");
					Class<?> taskClass = Message.getClass(
							clientSocket.socket(), (int) messLength);
					classLoader.loadClass(taskClass.getName());
					// TODO: ?
					// path = "bin/SumTask.class";
					// fc = new FileOutputStream(new File(path)).getChannel();
					// fc.transferFrom(clientSocket, 0, messLength);
					// Class<?> loadedClass =
					// classLoader.loadClass("application.sumTask");

				case ACK:
					System.out.println("READING ACK PACKET");
					break;
				case SERIALIZEDTASK:
					System.out.println("READING SERIALIZEDTASK PACKET");
					serializedTask = (Task) Message.getObject(clientSocket
							.socket());

					break;
				case EXEC:
					System.out.println("READING EXEC PACKET");
					long timeout;
					System.out
							.println("Timeout : " + (timeout = dis.readInt()));
					Thread computingThread = new Thread(
							serializedTask);
					computingThread.run();
					break;
				case EXECERROR:
					System.out.println("READING EXECERROR PACKET");
					break;
				case RESULT:
					System.out.println("READING RESULT PACKET");
					Object o1 = Message.getObject(clientSocket.socket());
					sumTask st = (sumTask) o1;
					System.out.println("RESULT : " + st.result);
					// TODO get Result
					break;
				case END:
					System.out.println("READING END CONNECTION PACKET");
					// TODO endConnection
					break;
				}
				// On lit le message :

				byte[] message = new byte[(int) messLength];
				System.out.println(new String(message, "UTF-16BE")
						+ "\nPACKET RECEIVED => Go to next one\n\n");
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// /**
	// *
	// * @param buff
	// * @return
	// */
	// public static Serializable unSerialize(ByteBuffer buff) {
	// try {
	// ByteArrayInputStream bais = new ByteArrayInputStream(buff.array(),
	// 0, buff.limit());
	// ObjectInputStream ois = new ObjectInputStream(bais);
	// return (Serializable) ois.readObject();
	// } catch (IOException | ClassNotFoundException e) {
	// System.out.println("ERREUR DESERIALISATION");
	// e.printStackTrace();
	// }
	// return null;
	// }

}