package network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Message {

	public static ByteBuffer bufferFromString(String s)
			throws CharacterCodingException {
		ByteBuffer buff = ByteBuffer.allocate(2 * s.length());
		buff.put(s.getBytes(StandardCharsets.UTF_16BE));
		return buff;
	}
	
	public static void bufferFromString(String s, ByteBuffer bf)
			throws CharacterCodingException {
		bf.clear();
		bf.rewind();
		bf.put(s.getBytes(StandardCharsets.UTF_16BE));
	}

	public static ByteBuffer bufferFromClass(String path) {
		FileChannel fc;
		try {
			fc = FileChannel.open(Paths.get(path));
			ByteBuffer fileBuff = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			fc.close();
			return fileBuff;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
