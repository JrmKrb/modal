package application;

import tasks.MergeSortCalculus;

public class TestMerge {

	public static void main(String[] args) {
		int[] tab = { 100, 50, 56, -10, 0, 0, 0, 1, 2, -10, -100, 100, 56, 56,
				55, 54, 59 };
		MergeSortCalculus ms = new MergeSortCalculus(tab);
		ms.compute();
		
		int[] tab2 = {-1,0,1,2,48,49,50,-3,-2,1,2,3,100};
		
		MergeSortCalculus ms2 = new MergeSortCalculus(tab2);
		ms2.merge(0,7,tab2.length-1);
	}
}
