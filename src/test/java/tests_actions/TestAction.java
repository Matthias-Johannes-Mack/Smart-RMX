package tests_actions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import action.Action;
import action.ActionMessageBit;
import action.ActionWait;

/**
 * Class that tests a Action
 *
 */
public class TestAction {

	@Test
	public void testAction() {
		int[] message1 = new int[] { 1, 1, 1 };
		int[] message2 = new int[] { 1, 1, 2 };

		ActionMessageBit actionMessageBit1 = new ActionMessageBit(message1);
		ActionMessageBit actionMessageBit2 = new ActionMessageBit(message2);
		ActionMessageBit actionMessageBit3 = new ActionMessageBit(message2);

		ActionWait actionWait1 = new ActionWait(1000);
		ActionWait actionWait2 = new ActionWait(2000);
		ActionWait actionWait3 = new ActionWait(2000);

		List<Action> actionList = new ArrayList<>();
		actionList.add(actionMessageBit1);
		actionList.add(actionMessageBit2);
		actionList.add(actionMessageBit3);
		actionList.add(actionWait1);
		actionList.add(actionWait2);
		// actionList.add(actionWait3); not added to the list

		// ----------- testing

		// asserts
		assertFalse(actionWait1.equals(actionWait2)); // false
		assertTrue(actionWait2.equals(actionWait3)); // true

		assertFalse(actionMessageBit1.equals(actionMessageBit2)); // false
		assertTrue(actionMessageBit2.equals(actionMessageBit3)); // true

		assertFalse(actionMessageBit1.equals(actionWait1)); // false
		assertFalse(actionWait1.equals(actionMessageBit1)); // false

		assertTrue(actionList.contains(actionWait3)); // true da ja zeit (2000) schon drin
		assertTrue(actionList.contains(actionMessageBit1)); // true
	}

}
