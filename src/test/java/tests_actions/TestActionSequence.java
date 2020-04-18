package tests_actions;

import static org.junit.Assert.*;

import org.junit.Test;

import action.actions.actionMessages.ActionMessageBit;
import action.actionSequence.ActionSequence;

/**
 * Class for testing the Action Sequence
 *
 */
public class TestActionSequence {

	@Test
	/**
	 * Method to test the Action Sequence
	 */
	public void testActionSequence() {
		int[] message1 = new int[] { 1, 1, 1 };
		int[] message2 = new int[] { 1, 1, 2 };
		int[] message3 = new int[] { 1, 1, 3 };
		int[] message4 = new int[] { 1, 1, 4 };

		ActionMessageBit action1 = new ActionMessageBit(message1);
		ActionMessageBit action2 = new ActionMessageBit(message2);
		ActionMessageBit action3 = new ActionMessageBit(message3);
		ActionMessageBit action4 = new ActionMessageBit(message4);

		// add 4 actions to the ActionSequence
		ActionSequence actionSequence = new ActionSequence();

		actionSequence.addAction(action1);
		actionSequence.addAction(action2);
		actionSequence.addAction(action3);
		actionSequence.addAction(action4);
		// 4
		assertEquals(4, actionSequence.getActionCount());
	}

}
