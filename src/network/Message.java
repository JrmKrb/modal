package network;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class Message {

	public static ByteBuffer bufferFromString(String s)
			throws CharacterCodingException {
		ByteBuffer buff = ByteBuffer.allocate(2*s.length());
		buff.put(s.getBytes(StandardCharsets.UTF_16BE));
		return buff;
	}
}
