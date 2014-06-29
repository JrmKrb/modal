package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import application.Task;

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
			ClassLoaderObjectInputStream in = new ClassLoaderObjectInputStream(classLoader, s.getInputStream());
			return (Object) in.readObject();
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendResult(final Task t, String remoteIP, short taskID) {
		final TCPClient remoteConnection = new TCPClient(taskID);
		remoteConnection.start();
		new Thread() {

			public void run() {
				try {
					Thread.sleep(1000);
					remoteConnection.intro();
					remoteConnection.sendBuff();
					remoteConnection.result(t);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

}
