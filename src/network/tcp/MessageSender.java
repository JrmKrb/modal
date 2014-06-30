package network.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import lib.Util;
import network.NetworkClass;

public class MessageSender extends NetworkClass {

	private ByteBuffer		writeBuff;
	private short			taskID;
	private SocketChannel	clientSocket;

	public MessageSender(ByteBuffer b, short t, SocketChannel c) {
		writeBuff = b;
		taskID = t;
		clientSocket = c;
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
			writeBuff.clear();
			return true;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	 * First packet
	 */
	public void sendIntro() {
		writeBuff.put(INTRO);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
		sendBuff();
	}

	/**
	 * Dependencies
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
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Task sender
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
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Serialized Task Sender
	 * 
	 * @throws IOException
	 */
	public void sendSerializedTask(Object t) {
		writeBuff.put(SERIALIZEDTASK);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut;
		try {
			oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(t);
			oOut.close();
		}
		catch (IOException e) {
			System.out.println("Error : Serialized Task.");
		}
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), t);
		sendBuff();
	}

	/**
	 * Asking for execution
	 */
	public void askForExecution() {
		writeBuff.put(EXEC);
		writeBuff.putShort(taskID);
		writeBuff.putLong(TIMEOUTLENGTH);
		writeBuff.putInt(TIMEOUT);
		sendBuff();
	}

	/**
	 * If error
	 * 
	 * @param error
	 */
	public void sendError(String error) {
		writeBuff.put(EXECERROR);
		writeBuff.putShort(taskID);
		Long n = (long) error.length() * 2;
		writeBuff.putLong(n);
		Util.bufferFromString(writeBuff, error);
		sendBuff();
	}

	/**
	 * Sending result
	 * 
	 * @param obj
	 * @throws IOException
	 */
	public void sendResult(Object result) throws IOException {
		writeBuff.put(RESULT);
		writeBuff.putShort(taskID);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(bOut);
		oOut.writeObject(result);
		oOut.close();
		writeBuff.putLong(bOut.toByteArray().length);
		sendBuff();
		Util.sendObject(clientSocket.socket(), result);
	}

	/**
	 * Asking for end of connection
	 */
	public void askForEnd() {
		writeBuff.put(END);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0);
		sendBuff();
	}


	/**
	 * Send Acknowledgment
	 * 
	 * @throws IOException
	 */
	public void ack() throws IOException {
		writeBuff.put(ACK);
		writeBuff.putShort(taskID);
		writeBuff.putLong(0L);
		writeBuff.flip();
		clientSocket.write(writeBuff);
		writeBuff.clear();
	}
}
