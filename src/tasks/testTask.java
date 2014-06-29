package tasks;



public class testTask implements Task {

	public int result;
	public void run() {
		System.out.println("Début task.");
		Doublet d = new Doublet(1,10);
		System.out.println("Doublet alloué.");
		result = d.a+d.b;
		System.out.println("Calcul effectué.");
	}
}