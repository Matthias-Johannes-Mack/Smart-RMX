/**
 *
 * @author Matthias Mack 3316380
 */
package matrixTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import action.Action;
import action.ActionSequence;
import action.ActionWait;
import bus.BusDepot;
import matrix.Matrix;

/**
 * Class that tests the matrix
 * 
 * @author Matthias Mack 3316380
 */
public class TestMatrix {

	@Test
	/**
	 * Function tests the Gauss calculation
	 */
	public void testCalcGauss() {
		// test the minus value
		assertEquals(0, Matrix.calcGauss(0));
		// formula : (n(n+1))/2)
		// for the -1
		assertEquals(0, Matrix.calcGauss(-1));
		// upper bounds
		assertEquals(179864061, Matrix.calcGauss(18966));
		// with the formula and a random number
		Random ran = new Random();
		int n = ran.nextInt(6) + 5;
		assertEquals(((n * (n + 1)) / 2), Matrix.calcGauss(n));
	}

	@Test
	/**
	 * Function tests the Bernds formula calculation (bus * 112) + (systemadresse *
	 * 8) + bit
	 */
	public void testCalcBerndsFormula() {
		// test the 0
		assertEquals(0, Matrix.calcBerndFormula(0, 0, 0));
		// formula : (n(n+1))/2)
		// for the -1
		assertEquals(-121, Matrix.calcBerndFormula(-1, -1, -1));
		// upper bounds
		assertEquals(1007, Matrix.calcBerndFormula(1, 111, 7));
		// the max bus 4, sysaddr 111, bit 7
		assertEquals(1343, Matrix.calcBerndFormula(4, 111, 7));
	}

	@Test
	/**
	 * Function tests the Bernds formula calculation (bus * 112) + (systemadresse *
	 * 8) + bit
	 */
	public void testTheWholeMatrix() {
		// create the actionlist
		ArrayList<Action> actionList = new ArrayList<>();
		// format <0x06><RMX><ADRRMX><VALUE>
		// create the needed arrays
		byte[] message = new byte[] { 6, 1, 0, 1 };
		Integer[] conditionOne = new Integer[] { 1, 0, 0 };
		Integer[] conditionTwo = new Integer[] { 1, 0, 0 };
		
		
		actionList.add(new ActionWait(1000));

		ActionSequence actionSequence = new ActionSequence();

		BusDepot busDepot = BusDepot.getBusDepot();
		busDepot.updateBus(message); // creates bus 1 and setzt bit 1 von systemadresse 1


		Matrix matrix = Matrix.getMatrix();
		matrix.addAction(conditionOne, conditionTwo, actionSequence);
		System.out.println("matrix: " + matrix.matrix[0].getActions());

		List<ActionSequence> result = matrix.check((byte) 1, (byte) 0, (byte) 1);

		for (ActionSequence actionSequence1 : result) {
			List<Action> actions = actionSequence.getActions();

			for (Action action : actions) {
				if (action instanceof ActionWait) {
					ActionWait waitaction = (ActionWait) action;
					System.out.println(waitaction.getWaitTime());
				}
			}
		}
	}

}
