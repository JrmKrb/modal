package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import application.Task;

public class TCPClient extends Thread implements NetworkInterface {

	private static ByteBuffer	writeBuff;
	public SocketChannel		clientSocket;

	private short				taskID;

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param taskID
	 */
	public TCPClient(short taskID) {
		this.taskID = taskID;
		writeBuff = ByteBuffer.allocate(1024000);
	}

	public void run() {
		try {
			clientSocket = SocketChannel.open();
			clientSocket.bind(new InetSocketAddress(0));
			InetSocketAddress remote = new InetSocketAddress(REMOTEIP, PORT);
			clientSocket.connect(remote);
			System.out.println("Client connected to " + remote.getAddress() + ":" + remote.getPort() + "\n");
			while (true)
				;
		}
		catch (IOException e) {
			System.out.println("ERREUR CONSTRUCTEUR TCP CLIENT");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendBuff() {
		try {
			writeBuff.flip();
			clientSocket.write(writeBuff);
			System.out.println("Buff sent");
			writeBuff.clear();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Début connection TCP
	 */
	public void intro() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
	}

	/**
	 * Envoi classes annexes
	 * 
	 * @param path
	 */
	public void sendClass(String path) {
		writeBuff.put(SIMPLECLASS);
		writeBuff.putShort(taskID);
		FileChannel fc;
		try {
			fc = FileChannel.open(Paths.get(path));
			writeBuff.putLong(fc.size());
			sendBuff();
			fc.transferTo(0, fc.size(), clientSocket);
		}
		catch (IOException e) {
			writeBuff.clear();
			e.printStackTrace();
		}
	}

	/**
	 * Envoi Tâche
	 * 
	 * @param path
	 */
	public void sendTask(String path) {
		FileChannel fc = Util.channelFromFile(path);
		writeBuff.put(TASKCLASS);
		writeBuff.putShort(taskID);
		try {
			writeBuff.putLong(fc.size());
			sendBuff();
			fc.transferTo(0, fc.size(), clientSocket);
		}
		catch (IOException e) {
			writeBuff.clear();
			e.printStackTrace();
		}
	}

	/**
	 * Envoi Acknowledgment
	 */
	public void ack() {
		writeBuff.put(ACK);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
	}

	/**
	 * Envoi tâche sérialisée (avec données)
	 * 
	 * @throws IOException
	 */
	public void serializedTask(Task t) throws IOException {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(t);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), t);
	}

	/**
	 * Exécution effectuée
	 */
	public void execute() {
		writeBuff.put(EXEC);
		writeBuff.putShort(taskID);
		writeBuff.putLong(TIMEOUTLENGTH);
		writeBuff.putInt(TIMEOUT);
	}

	/**
	 * Erreur lors de l'exécution
	 * 
	 * @param error
	 */
	public void error(String error) {
		writeBuff.put(EXECERROR);
		writeBuff.putShort(taskID);
		Long n = (long) error.length() * 2;
		writeBuff.putLong(n);
		Util.bufferFromString(writeBuff, error);
	}

	/**
	 * Envoi du résultat
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public void result(Serializable obj) throws IOException {
		writeBuff.put(RESULT);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(obj);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), obj);
	}

	/**
	 * Fin de connection
	 */
	public void end() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
	}

}