package application;
import java.io.IOException;

public class testServer {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {

		Server TCPserver = new Server();
		TCPserver.start();

	}
}