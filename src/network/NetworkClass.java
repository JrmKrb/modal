package network;

public abstract class NetworkClass extends Thread {

	protected final static byte	INTRO			= 0;
	protected final static byte	SIMPLECLASS		= 1;
	protected final static byte	TASKCLASS		= 2;
	protected final static byte	ACK				= 3;
	protected final static byte	SERIALIZEDTASK	= 4;
	protected final static byte	EXEC			= 5;
	protected final static byte	EXECERROR		= 6;
	protected final static byte	RESULT			= 7;
	protected final static byte	END				= 8;

	protected final static int	TIMEOUTLENGTH	= 4;
	protected final static int	TIMEOUT			= 5;

	protected final static byte	FREE			= 0;
	protected final static byte	BUSY			= 1;

	protected final static int	PORT			= 12347;

}
