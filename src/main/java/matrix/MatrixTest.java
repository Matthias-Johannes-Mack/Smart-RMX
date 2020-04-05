package matrix;

import java.util.Arrays;

public class MatrixTest {

	public static void main(String[] args) {
//		System.out.println(Matrix.getMatrix().matrix.length);
//		Object[] o = Matrix.getMatrix().matrix;
//		System.out.println(o[3]);+

		Integer[] conditionOne = new Integer[] {0, 0, 4};
		Integer[] conditionTwo = new Integer[] {0, 0, 0};

		Matrix.addAction(conditionTwo, conditionOne);
	}

}
