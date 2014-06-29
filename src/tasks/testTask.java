package tasks;

public class testTask implements Task {

	private static final long	serialVersionUID	= -4321384327705705026L;
	public int					result;

	public void run() {
		System.out.println("Début du calcul.");
		Doublet d = new Doublet(1, 10);
		System.out.println("Doublet alloué.");
		result = d.a + d.b;
		System.out.println("Calcul effectué.");
	}
}