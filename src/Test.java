import network.UDP;


public class Test {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		UDP serv = new UDP();
		serv.whoIsOnline();
	}

}