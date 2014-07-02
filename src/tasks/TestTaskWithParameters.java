package tasks;

public class TestTaskWithParameters implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7276296688999773234L;
	public long result = 1L;
	public int i;
	public int j;
	public int p;

	public TestTaskWithParameters(int a, int b, int pr) {
		i = a;
		j = b;
		p = pr;
	}

	public void run() {
		System.out.println("Begin computation " + i + " " + j + " " + p);
		for (int k = i; k <= j; k++)
			result = (result * k) % p;
		
		System.out.println("Computation done");
	}
}