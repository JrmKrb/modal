package lib;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

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
	 * Get an Object from Socket s, using an external ClassLoader for dependencies
	 * 
	 * @param s
	 * @param classLoader
	 * @return the object
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

}
