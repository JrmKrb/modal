package network;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Message {

	public static void bufferFromString(ByteBuffer writeBuff, String message) {
		writeBuff.put(message.getBytes(StandardCharsets.UTF_16BE));
	}

	public static FileChannel channelFromFile(String path) {
		try {
			return FileChannel.open(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendObject(Socket s, Object o) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object getObject(Socket s) {
		try {
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			return (Object) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClass(Socket s, int length) {
		byte[] bf = new byte[length];
		try {
			s.getInputStream().read(bf);
			NetworkClassLoader classLoader = new NetworkClassLoader(
					Message.class.getClassLoader());
			return classLoader.load(bf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
