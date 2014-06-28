package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

	
	public static Class<?> getClass(Socket s, int length) {
		byte[] bf = new byte[length];
		try {
			s.getInputStream().read(bf);
			NetworkClassLoader classLoader = new NetworkClassLoader(
					TCPServer.class.getClassLoader());
			return classLoader.load(bf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("129.104.252.48", PORT));
			System.out.println("Serveur en attente d'un client.");
			clientSocket = serverSocket.accept();
			System.out.println("Serveur en attente d'un message.");
			DataInputStream dis = new DataInputStream(clientSocket.socket()
					.getInputStream());
			Task serializedTask = null;
			while (true) {
				System.out.println("Waiting for Type");
				byte tempType = dis.readByte();
				System.out.println(tempType);
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

					Class<?> simpleClass = getClass(
							clientSocket.socket(), (int) messLength);
					classLoader.resolve(simpleClass);
//					classLoader.loadClass(simpleClass.getName());
					break;
				case TASKCLASS:
					System.out.println("READING TASKCLASS PACKET");
					Class<?> taskClass = getClass(
							clientSocket.socket(), (int) messLength);
					classLoader.resolve(taskClass);
					System.out.println("taskClass " + taskClass.getName() + " loaded");
					//classLoader.loadClass(taskClass.getName());
					messLength = 0;
					
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
					ClassLoaderObjectInputStream in = new ClassLoaderObjectInputStream(classLoader,clientSocket.socket().getInputStream());
					System.out.println("try to read object");
					serializedTask  = (Task) in.readObject();
//					serializedTask = (Task) Message.getObject(clientSocket
//							.socket());
					break;
				case EXEC:
					System.out.println("READING EXEC PACKET");
					long timeout;
					System.out
							.println("Timeout : " + (timeout = dis.readInt()));
					serializedTask.run();
					TCPClient t = new TCPClient("129.104.252.49",(short) 1337);
					t.start();
					Thread.sleep(3000);
					t.result(serializedTask);
					t.sendBuff();
					messLength = 0;
					break;
				case EXECERROR:
					System.out.println("READING EXECERROR PACKET");
					break;
				case RESULT:
					System.out.println("READING RESULT PACKET");
					sumTask o1 = (sumTask) Message.getObject(clientSocket.socket(),classLoader);
					System.out.println("Resultat Recu : "+o1.result);
					break;
				case END:
					System.out.println("READING END CONNECTION PACKET");
					// TODO endConnection
					break;
				}
				// On lit le message :

				byte[] message = new byte[(int) messLength];
			dis.read(message);
				System.out.println(new String(message, "UTF-16BE")
						+ "\nPACKET RECEIVED => Go to next one\n\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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