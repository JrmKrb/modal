package network;
import java.nio.ByteBuffer;

public class Message {
	public static ByteBuffer bufferFromString(String s) {
		int n = s.length();
		ByteBuffer buff = ByteBuffer.allocate(2 * n);
		for (int i = 0; i < n; i++) {
			buff.putChar(s.charAt(i));
		}
		return buff;
	}
}
