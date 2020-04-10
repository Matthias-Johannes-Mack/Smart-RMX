
package tests_matrix;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

import matrix.MatrixUtil;
import org.junit.Test;

import action.Action;
import action.ActionSequence;
import action.ActionWait;
import bus.BusDepot;
import matrix.BitMatrix;

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
		assertEquals(0, MatrixUtil.calcGauss(0));
		// formula : (n(n+1))/2)
		// for the -1
		assertEquals(0, MatrixUtil.calcGauss(-1));
		// upper bounds
		assertEquals(179864061, MatrixUtil.calcGauss(18966));
		// with the formula and a random number
		Random ran = new Random();
		int n = ran.nextInt(6) + 5;
		assertEquals(((n * (n + 1)) / 2), MatrixUtil.calcGauss(n));
	}

	@Test
	/**
	 * Function tests the Bernds formula calculation (bus * 112) + (systemadresse *
	 * 8) + bit
	 */
	public void testCalcBerndsFormula() {
		// test the 0
		assertEquals(0, MatrixUtil.calcBitIndex(0, 0, 0));
		// formula : (n(n+1))/2)
		// for the -1
		assertEquals(-121, MatrixUtil.calcBitIndex(-1, -1, -1));
		// upper bounds
		assertEquals(1007, MatrixUtil.calcBitIndex(1, 111, 7));
		// the max bus 4, sysaddr 111, bit 7
		assertEquals(1343, MatrixUtil.calcBitIndex(4, 111, 7));
	}

	/**
	 * Method that tests the matrix as a component
	 * 
	 * @author Angelo
	 */
	@Test
	public void testMatrix() {

		BusDepot busDepot = BusDepot.getBusDepot();
		busDepot.updateBus((byte) 1, (byte) 0x6f, (byte) 0);

		// ActionSequence that contains ActionWait with waitTime 1
		Action actionWait1 = new ActionWait(1);
		ActionSequence actionSequence1 = new ActionSequence();
		actionSequence1.addAction(actionWait1);

		Action actionWait2 = new ActionWait(0);
		ActionSequence actionSequence2 = new ActionSequence();
		actionSequence2.addAction(actionWait2);

		// [Bus][Systemadresse][Bit][BitValue]
		Integer[] conditionOne = new Integer[] { 1, 111, 0, 1 };
		Integer[] conditionTwo = new Integer[] { 1, 111, 0, 1 };

		Integer[] conditionThree = new Integer[] { 1, 111, 0, 0 };
		Integer[] conditionFour = new Integer[] { 1, 111, 0, 0 };

		BitMatrix matrix = BitMatrix.getMatrix();
		matrix.addAction(conditionOne, conditionTwo, actionSequence1); // sollte zu wrapper hinzugefügt werden an 1-1
		matrix.addAction(conditionThree, conditionFour, actionSequence2); // sollte zu wrapper hinzugefügt werden an
																			// 0-0

		List<ActionSequence> resultCheckAll = matrix.checkAllFields();
		for (ActionSequence actionSequence : resultCheckAll) {
			List<Action> actions = actionSequence.getActions();

			for (Action action : actions) {
				if (action instanceof ActionWait) {
					ActionWait waitaction = (ActionWait) action;
					System.err.println(waitaction.getWaitTime()); // 0
				}
			}
		}

		// format <0x06><RMX><ADRRMX><VALUE>
		byte[] message1 = new byte[] { 6, 1, 111, 1 };
		Integer[] changes1 = busDepot.getChangesAndUpdate((byte) 1, message1[2], message1[3]); // bit 0
																												// wurde
																												// auf 1
																												// gesetzt

		List<ActionSequence> resultCheck = matrix.check(message1[1], message1[2], changes1);
		for (ActionSequence actionSequence : resultCheck) {
			List<Action> actions = actionSequence.getActions();

			for (Action action : actions) {
				if (action instanceof ActionWait) {
					ActionWait waitaction = (ActionWait) action;
					System.err.println(waitaction.getWaitTime()); // 1
				}
			}
		}

	}
}
