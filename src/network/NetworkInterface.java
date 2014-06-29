package network;

public interface NetworkInterface {

	final static byte	INTRO			= 0;
	final static byte	SIMPLECLASS		= 1;
	final static byte	TASKCLASS		= 2;
	final static byte	ACK				= 3;
	final static byte	SERIALIZEDTASK	= 4;
	final static byte	EXEC			= 5;
	final static byte	EXECERROR		= 6;
	final static byte	RESULT			= 7;
	final static byte	END				= 8;

	final static int	TIMEOUTLENGTH	= 4;
	final static int	TIMEOUT			= 30;

	final static int	PORT			= 12357;
	final static String	REMOTEIP		= "129.104.221.49" ;

}
