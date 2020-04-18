package tests_actions;

import static org.junit.Assert.*;

import org.junit.Test;

import action.ActionDepot;
import action.actions.actionMessages.ActionMessageBit;
import action.actions.ActionWait;

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
		ActionMessageBit actionMessageBit1 = new ActionMessageBit(message1);
		ActionMessageBit actionMessageBit2 = new ActionMessageBit(message1);
		// create wait actions
		ActionWait actionWait1 = new ActionWait(1000);
		ActionWait actionWait2 = new ActionWait(1000);

		// create the action depot
		ActionDepot actionDepot = ActionDepot.getActionDepot();

		// == to compare the reference
		// true
		assertTrue(actionMessageBit1 == actionDepot.addAction(actionMessageBit1));
		assertTrue(actionWait1 == actionDepot.addAction(actionWait1));
		// false
		assertFalse(actionMessageBit2 == actionDepot.addAction(actionMessageBit2));
		assertFalse(actionWait2 == actionDepot.addAction(actionWait2));
	}

}
