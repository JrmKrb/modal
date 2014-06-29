package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server extends NetworkInterface {

	private ServerSocketChannel	serverSocket;

	// private LinkedList<TCPServer> socketsList;

	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 12347));
			System.out.println("New Server listening on " + serverSocket.getLocalAddress());
			while (true) {
				SocketChannel clientSocket = serverSocket.accept();
				System.out.println("New connection from : " + clientSocket.getRemoteAddress());
				TCPServer temp = new TCPServer(clientSocket);
				// socketsList.add(temp);
				temp.start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
