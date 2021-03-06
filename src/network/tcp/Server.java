package network.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import network.NetworkClass;

public class Server extends NetworkClass {

	private ServerSocketChannel		serverSocket;
	private LinkedList<TCPServer>	socketsList;
	private InetSocketAddress		serverISA;

	public Server() {
		socketsList = new LinkedList<TCPServer>();
		try {
			serverISA = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
		}
		catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param isa
	 */
	public Server(InetSocketAddress isa) {
		socketsList = new LinkedList<TCPServer>();
		this.serverISA = isa;
	}

	@Override
	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(serverISA);
			System.out.println("New Server listening on: " + serverSocket.getLocalAddress());
			while (true) {
				SocketChannel clientSocket = serverSocket.accept();
				System.out.println("New connection from: " + clientSocket.getRemoteAddress());
				TCPServer temp = new TCPServer(clientSocket);
				socketsList.add(temp);
				temp.start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
