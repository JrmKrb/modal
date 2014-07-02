package tasks;

public class MergeSortCalculus {
	public int[] tab;

	public MergeSortCalculus(int[] t) {
		tab = t;
	}

	public void compute() {
		print(0, tab.length - 1, tab);
		sort(0, tab.length - 1);
		print(0, tab.length - 1, tab);
	}

	public void sort(int i, int j) {
		if (i > j)
			System.err.println("ERREUR");
		else if (i == j)
			return;
		else if (i + 1 == j) {
			if (tab[i] > tab[i + 1])
				swap(i, i + 1);
		} else {
			int a = (i + j) / 2;
			sort(i, a);
			sort(a + 1, j);
			merge(i, a + 1, j);
		}
	}

	public void merge(int i, int j, int k) {
		int n = k - i + 1;
		int g = i;
		int d = j;
		int[] temp = new int[n];
		int count = 0;
		while (count < n && g <= j - 1 && d <= k) {
			if (tab[g] <= tab[d]) {
				temp[count] = tab[g];
				g++;
			} else {
				temp[count] = tab[d];
				d++;
			}
			count++;
		}
		if (g <= j - 1) {
			for (int l = g; l <= j - 1; l++) {
				temp[count] = tab[l];
				count++;
			}
		} else if (d <= k) {
			for (int l = d; l <= k; l++) {
				temp[count] = tab[l];
				count++;
			}
		}
		for (int m = 0; m < n; m++)
			tab[i + m] = temp[m];

	}

	public void swap(int i, int j) {
		int x = tab[i];
		tab[i] = tab[j];
		tab[j] = x;
	}

	public static void print(int a, int b, int[] tabToPrint) {
		System.out.println();
		System.out.print(tabToPrint[a]);
		for (int i = a + 1; i <= b; i++)
			System.out.print("  " + tabToPrint[i]);
		System.out.println();
	}
}
