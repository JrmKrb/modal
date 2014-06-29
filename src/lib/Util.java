package lib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import network.tcp.TCPClient;
import tasks.Task;

public class Util {

	/**
	 * Fill a ByteBuffer with a string
	 * 
	 * @param writeBuff
	 * @param message
	 */
	public static void bufferFromString(ByteBuffer writeBuff, String message) {
		writeBuff.put(message.getBytes(StandardCharsets.UTF_16BE));
	}

	/**
	 * Return a FileChannel
	 * 
	 * @param path
	 * @return
	 */
	public static FileChannel channelFromFile(String path) {
		try {
			return FileChannel.open(Paths.get(path));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Send object o through socket s
	 * 
	 * @param s
	 * @param o
	 */
	public static void sendObject(Socket s, Object o) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(o);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get an Object from Socket s, using an external ClassLoader for dependancies
	 * 
	 * @param s
	 * @param classLoader
	 * @return
	 */
	public static Object getObject(Socket s, NetworkClassLoader classLoader) {
		try {
			return (Object) new ClassLoaderObjectInputStream(classLoader, s.getInputStream()).readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Send a finished Task
	 * 
	 * @param t
	 * @param remoteIP
	 * @param taskID
	 */
	public static void sendResult(final Task t, InetSocketAddress remoteIP, short taskID) {
		System.out.println("Trying to connect to " + remoteIP + "in order to send result.");
		final TCPClient remoteConnection = new TCPClient(taskID, remoteIP);
		remoteConnection.start();
		try {
			Thread.sleep(1000);
			remoteConnection.intro();
			remoteConnection.sendBuff();
			remoteConnection.result(t);
			System.out.println("Result sent with success !");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
