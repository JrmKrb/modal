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
import network.NetworkInterface;
import tasks.Task;
import application.TestClient;

public class TCPServer extends NetworkInterface {

	private SocketChannel	clientSocket;
	private ByteBuffer		writeBuff;

	/**
	 * @param c
	 */
	public TCPServer(SocketChannel c) {
		clientSocket = c;
		writeBuff = ByteBuffer.allocate(1024000);
	}

	/**
	 * 
	 */
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
				System.out.println("taskID : " + tempTaskID + "\n");
				long messLength = dis.readLong();
				System.out.println("Message length : " + messLength);
				switch (tempType) {
					case INTRO:
						System.out.println("READING INTRO PACKET");
						break;
					case SIMPLECLASS:
						System.out.println("READING SIMPLECLASS PACKET");
						Class<?> simpleClass = getClass(clientSocket.socket(), (int) messLength, classLoader);
						classLoader.addClass(simpleClass);
						System.out.println("simpleClass " + simpleClass.getName() + " loaded.");
						messLength = 0;
						break;
					case TASKCLASS:
						System.out.println("READING TASKCLASS PACKET");
						Class<?> taskClass = getClass(clientSocket.socket(), (int) messLength, classLoader);
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
						System.out.println("Timeout : " + dis.readInt());
						serializedTask.run();
						Thread resultSenderThread = new TCPClient((short) tempTaskID, (InetSocketAddress) clientSocket.getRemoteAddress(), serializedTask);
						resultSenderThread.start();
						resultSenderThread.join();
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
						in = new ClassLoaderObjectInputStream(classLoader, clientSocket.socket().getInputStream());
						TestClient.treatResult((Task) in.readObject());
						messLength = 0;
						break;
					case END:
						System.out.println("READING END CONNECTION PACKET");
						running = false;
						break;
					default:
						System.out.println("Wrong packet received. Nothing is done.");
						break;
				}

				// Reading message :
				if (messLength != 0) {
					byte[] message = new byte[(int) messLength];
					dis.read(message);
					System.out.print(new String(message, "UTF-16BE"));
				}
				System.out.print("\nENDPACKET\n\n");
				if (running) ack(tempTaskID, clientSocket);
			}
		}
		catch (EOFException e) {
			System.out.println("Connexion terminée : "+e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IOEXception : " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.out.println("Classe non trouvée :\n" + e.getMessage());
		}
		catch (InterruptedException e) {
			System.out.println("Thread d'envoi de résultat interrompu : "+e.getMessage());
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
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Envoi Acknowledgment
	 * 
	 * @throws IOException
	 */
	public void ack(short taskID, SocketChannel s) throws IOException {
		writeBuff.put(ACK);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
		writeBuff.flip();
		s.write(writeBuff);
		writeBuff.clear();
	}
}