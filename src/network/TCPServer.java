package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import application.Server;
import application.Task;

public class TCPServer extends Thread implements NetworkInterface {

	private SocketChannel	clientSocket;
	private Task			result	= null;

	public TCPServer(SocketChannel c) {
		clientSocket = c;
	}

	public void run() {
		try {
			DataInputStream dis = new DataInputStream(clientSocket.socket().getInputStream());
			Task serializedTask = null;
			NetworkClassLoader classLoader = new NetworkClassLoader(TCPServer.class.getClassLoader());
			System.out.println("Serveur en attente d'un message.");
			while (true) {
				byte tempType = dis.readByte();
				short tempTaskID = dis.readShort();
				System.out.println("taskID : " + tempTaskID + "\n");
				long messLength = dis.readLong();
				System.out.println("Taille du message : " + messLength);
				switch (tempType) {
					case INTRO:
						System.out.println("READING INTRO PACKET");
						break;
					case SIMPLECLASS:
						System.out.println("READING SIMPLECLASS PACKET");
						Class<?> simpleClass = getClass(clientSocket.socket(), (int) messLength);
						classLoader.addClass(simpleClass);
						break;
					case TASKCLASS:
						System.out.println("READING TASKCLASS PACKET");
						Class<?> taskClass = getClass(clientSocket.socket(), (int) messLength);
						classLoader.addClass(taskClass);
						System.out.println("taskClass " + taskClass.getName() + " loaded.");
						messLength = 0;
						break;
					case ACK:
						System.out.println("READING ACK PACKET");
						break;
					case SERIALIZEDTASK:
						System.out.println("READING SERIALIZEDTASK PACKET");
						ClassLoaderObjectInputStream in = new ClassLoaderObjectInputStream(classLoader, clientSocket.socket().getInputStream());
						System.out.println("Try to read object : ");
						serializedTask = (Task) in.readObject();
						messLength = 0;
						break;
					case EXEC:
						System.out.println("READING EXEC PACKET");
						long timeout;
						System.out.println("Timeout : " + (timeout = dis.readInt()));
						serializedTask.run();
						result = serializedTask;
						messLength = 0;
						break;
					case EXECERROR:
						System.out.println("READING EXECERROR PACKET");
						byte[] message = new byte[(int) messLength];
						dis.read(message);
						System.err.println(new String(message, "UTF-16BE"));
						messLength = 0;
						break;
					case RESULT:
						System.out.println("READING RESULT PACKET");
						Util.sendResult(serializedTask, clientSocket.getRemoteAddress().toString(), tempTaskID);
						break;
					case END:
						System.out.println("READING END CONNECTION PACKET");
						// TODO endConnection
						break;
				}

				// Reading message :

				byte[] message = new byte[(int) messLength];
				dis.read(message);
				System.out.println(new String(message, "UTF-16BE") + "\nPACKET RECEIVED => Waiting for another one\n\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getClass(Socket s, int length) {
		byte[] bf = new byte[length];
		try {
			s.getInputStream().read(bf);
			NetworkClassLoader classLoader = new NetworkClassLoader(TCPServer.class.getClassLoader());
			return classLoader.load(bf);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}