package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCP {
	ServerSocketChannel serverSocket;
	SocketChannel clientSocket;
	
	public TCP(String ip, int port) {
		connectClient(ip,port);
	}
	
	public void connectClient(String ip, int port) {
		try {
			InetSocketAddress dest = new InetSocketAddress(ip,port);
			clientSocket = SocketChannel.open();
			clientSocket.connect(dest);
		} catch (IOException e) {
			System.out.println("ERREUR connectClient : "+ip+":"+port);
			e.printStackTrace();
		}
		
	}

	// For Client
	public void sendClass(String path) throws IOException {
		FileChannel fisC;
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			fisC = fis.getChannel();
			ByteBuffer buff = ByteBuffer.allocate((int) fisC.size());
			fisC.read(buff);
			clientSocket.write(buff);
			fisC.close();
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("Ficher "+path+"non trouvé !");
			e.printStackTrace();
		}
	}
	
	
	// For Client
	public void sendSerializedClass(String path) {}
	
	// For Client & Servers
	public void ack() {}
	
	// For Servers
	public void sendResult() {}
	
	// For Clients
	public void endOfTask() {
		
	}
	
}
