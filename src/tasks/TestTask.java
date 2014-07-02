package tasks;

public class TestTask implements Task {

	private static final long	serialVersionUID	= -4321384327705705026L;
	public int					result;

	public void run() {
		System.out.println("Begin computation");
		Pair d = new Pair(1, 10);
		System.out.println("Pair allocated");
		//try {
		//	Thread.sleep(10000);
		//}
		//catch (InterruptedException e) {}
		result = d.a + d.b;
		System.out.println("Computation done");
	}
}