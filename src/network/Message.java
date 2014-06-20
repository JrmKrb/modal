package network;

import java.io.IOException;
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
}
