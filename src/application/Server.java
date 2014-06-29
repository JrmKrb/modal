package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import network.NetworkInterface;
import network.TCPServer;

public class Server extends Thread implements NetworkInterface {

	private ServerSocketChannel	serverSocket;

	// private LinkedList<TCPServer> socketsList;

	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT));
			System.out.println("Nouveau serveur, écoutant sur " + serverSocket.getLocalAddress());
			while (true) {
				SocketChannel clientSocket = serverSocket.accept();
				System.out.println("Nouvelle connexion de : " + clientSocket.getRemoteAddress());
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
