package application;

public class sumTask implements Task {

	public int result = 0;

	@Override
	public void run() {
		result = 2 + 2;
	}

}
