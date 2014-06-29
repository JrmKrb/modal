package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import tasks.Task;

public class Util {

	public static void bufferFromString(ByteBuffer writeBuff, String message) {
		writeBuff.put(message.getBytes(StandardCharsets.UTF_16BE));
	}

	public static FileChannel channelFromFile(String path) {
		try {
			return FileChannel.open(Paths.get(path));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendObject(Socket s, Object o) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(o);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object getObject(Socket s, NetworkClassLoader classLoader) {
		try {
			return (Object) new ClassLoaderObjectInputStream(classLoader, s.getInputStream()).readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendResult(final Task t, InetSocketAddress remoteIP, short taskID) {
		System.out.println("Tentative de connection sur " + remoteIP + ".");
		final TCPClient remoteConnection = new TCPClient(taskID, remoteIP);
		remoteConnection.start();
		try {
			Thread.sleep(1000);
			remoteConnection.intro();
			remoteConnection.sendBuff();
			remoteConnection.result(t);
			System.out.println("Résultat envoyé !");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
