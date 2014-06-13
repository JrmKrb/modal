package network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Message {

	public static void bufferFromString(ByteBuffer writeBuff, String message) {
		writeBuff.put(message.getBytes(StandardCharsets.UTF_16BE));
	}

	public static ByteBuffer bufferFromClass(String path, ByteBuffer writeBuff) {
		FileChannel fc;
		try {
			fc = FileChannel.open(Paths.get(path));
			writeBuff.putLong(fc.size());
			ByteBuffer fileBuff = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					writeBuff.capacity());
			fc.close();
			return fileBuff;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
