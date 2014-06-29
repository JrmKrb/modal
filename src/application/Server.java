package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import network.TCPServer;

public class Server {

	private ServerSocketChannel		serverSocket;
	private LinkedList<TCPServer>	socketsList;
	private final static int		PORT	= 12357;

	public void start() throws IOException {
		serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT));
		System.out.println("Nouveau serveur, écoutant sur " + serverSocket.getLocalAddress());
		while (true) {
			SocketChannel clientSocket = serverSocket.accept();
			System.out.println("Nouvelle connexion de : " + clientSocket.getRemoteAddress());
			TCPServer temp = new TCPServer(clientSocket);
			socketsList.add(temp);
		}
	}
}
