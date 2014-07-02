package tasks;

public class TestTaskMerge implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1229584856281710232L;
	public long result = 1L;
	public int i;
	public int j;
	public MergeSortCalculus ms;

	public TestTaskMerge(int a, int b, int[] tab) {
		i = a;
		j = b;
		ms = new MergeSortCalculus(tab);
	}

	public void run() {
		System.out.println("Begin computation " + i + " " + j);
		ms.compute();
		System.out.println("Computation done");
	}
}