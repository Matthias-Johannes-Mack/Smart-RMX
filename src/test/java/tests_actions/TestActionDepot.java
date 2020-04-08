package tests_actions;

import static org.junit.Assert.*;

import org.junit.Test;

import action.ActionDepot;
import action.ActionMessage;
import action.ActionWait;

/**
 * Class for testing the Action Depot
 *
 */
public class TestActionDepot {

	@Test
	/**
	 * Test for the Action depot
	 */
	public void testActionDepot() {
		// the int array of the message
		int[] message1 = new int[] { 1, 1, 1 };
		// create message actions
		ActionMessage actionMessage1 = new ActionMessage(message1);
		ActionMessage actionMessage2 = new ActionMessage(message1);
		// create wait actions
		ActionWait actionWait1 = new ActionWait(1000);
		ActionWait actionWait2 = new ActionWait(1000);

		// create the action depot
		ActionDepot actionDepot = ActionDepot.getActionDepot();

		// == to compare the reference
		// true
		assertTrue(actionMessage1 == actionDepot.addAction(actionMessage1));
		assertTrue(actionWait1 == actionDepot.addAction(actionWait1));
		// false
		assertFalse(actionMessage2 == actionDepot.addAction(actionMessage2));
		assertFalse(actionWait2 == actionDepot.addAction(actionWait2));
	}

}
