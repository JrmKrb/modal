package network.tcp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import lib.ClassLoaderObjectInputStream;
import lib.NetworkClassLoader;
import network.NetworkClass;
import tasks.Task;
import application.TestConsumer;

public class TCPServer extends NetworkClass {

	private SocketChannel	clientSocket;
	private MessageSender	m;

	/**
	 * @param c
	 */
	public TCPServer(SocketChannel c) {
		clientSocket = c;
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(clientSocket.socket().getInputStream());
			Task serializedTask = null;
			NetworkClassLoader classLoader = new NetworkClassLoader(TCPServer.class.getClassLoader());
			System.out.println("TCPServer waiting for message from " + clientSocket.getRemoteAddress());
			boolean running = true;
			while (running) {
				byte tempType = dis.readByte();
				short tempTaskID = dis.readShort();
				if (m == null) m = new MessageSender(ByteBuffer.allocate(1024000), tempTaskID, clientSocket);
				long messLength = dis.readLong();
				System.out.println(tempTaskID+" - Message length: " + messLength);
				switch (tempType) {
					case INTRO:
						System.out.println(tempTaskID+" - READING INTRO PACKET");
						break;
					case SIMPLECLASS:
						System.out.println(tempTaskID+" - READING SIMPLECLASS PACKET");
						Class<?> simpleClass = getClass(clientSocket.socket(), (int) messLength, classLoader);
						classLoader.addClass(simpleClass);
						System.out.println(tempTaskID+"simpleClass " + simpleClass.getName() + " loaded.");
						messLength = 0;
						break;
					case TASKCLASS:
						System.out.println(tempTaskID+" - READING TASKCLASS PACKET");
						Class<?> taskClass = getClass(clientSocket.socket(), (int) messLength, classLoader);
						classLoader.addClass(taskClass);
						System.out.println(tempTaskID+" - taskClass " + taskClass.getName() + " loaded.");
						messLength = 0;
						break;
					case ACK:
						System.out.println(tempTaskID+" - READING ACK PACKET");
						break;
					case SERIALIZEDTASK:
						System.out.println(tempTaskID+" - READING SERIALIZEDTASK PACKET");
						ClassLoaderObjectInputStream in = new ClassLoaderObjectInputStream(classLoader, clientSocket.socket().getInputStream());
						System.out.println(tempTaskID+" - Try to read object : ");
						serializedTask = (Task) in.readObject();
						messLength = 0;
						break;
					case EXEC:
						System.out.println(tempTaskID+" - READING EXEC PACKET");
						int timeOut = dis.readInt();
						Thread serializedTaskThread = new Thread(serializedTask);
						// long currentTime = java.lang.System.currentTimeMillis();
						serializedTaskThread.start();
						/*
						 * while (!serializedTaskThread.isAlive() && java.lang.System.currentTimeMillis() - currentTime
						 * < timeOut * 1000) {} if (serializedTaskThread.isAlive()) { serializedTaskThread.interrupt();
						 * System.out.println("Timeout exceeded."); m.sendError("Timeout exceeded."); } else {
						 */
						serializedTaskThread.join();
						Thread resultSenderThread = new TCPClient((short) tempTaskID, (InetSocketAddress) clientSocket.getRemoteAddress(), serializedTask);
						resultSenderThread.start();
						// }
						messLength = 0;
						break;
					case EXECERROR:
						System.out.println(tempTaskID+" - READING EXECERROR PACKET");
						byte[] message = new byte[(int) messLength];
						dis.read(message);
						System.err.println(new String(message, "UTF-16BE"));
						messLength = 0;
						break;
					case RESULT:
						System.out.println(tempTaskID+" - READING RESULT PACKET");
						in = new ClassLoaderObjectInputStream(classLoader, clientSocket.socket().getInputStream());
						TestConsumer.treatResult((Task) in.readObject());
						messLength = 0;
						break;
					case END:
						System.out.println(tempTaskID+" - READING END CONNECTION PACKET");
						running = false;
						break;
					default:
						System.out.println(tempTaskID+" - Wrong packet received. Nothing is done.");
						break;
				}

				/* Reading message */
				if (messLength != 0) {
					byte[] message = new byte[(int) messLength];
					dis.read(message);
					System.out.print(tempTaskID+" - "+new String(message, "UTF-16BE"));
				}
				System.out.println("ENDPACKET");
				if (running) m.ack();
			}
		}
		catch (EOFException e) {
			System.out.println("Connexion terminée : " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IOEXception : " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.out.println("Classe non trouvée : "+e.getMessage());
		}
		catch (InterruptedException e) {
			System.out.println("Thread Interrompu : "+e.getMessage());
		}
		finally {
			try {
				clientSocket.close();
			}
			catch (IOException e) {}
		}
	}

	/**
	 * @param s
	 * @param length
	 * @param nlc
	 * @return
	 */
	public static Class<?> getClass(Socket s, int length, NetworkClassLoader nlc) {
		byte[] bf = new byte[length];
		try {
			s.getInputStream().read(bf);
			return nlc.load(bf);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}