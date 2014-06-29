package application;


public class testTask implements Task {

	int result;
	public void run() {
		Doublet d = new Doublet(1,10);
		result = d.a+d.b;
	}
}