package tests_actions;

import static org.junit.Assert.*;

import org.junit.Test;

import action.ActionSequence;
import action.ActionSequenceWrapper;
import action.ActionWait;

public class TestActionSequenceWrapper {

	@Test
	public void testActionSequenceWrapper() {
		// create the wait actions
		ActionWait actionWait1 = new ActionWait(0);
		ActionWait actionWait2 = new ActionWait(1);
		ActionWait actionWait3 = new ActionWait(2);
		ActionWait actionWait4 = new ActionWait(3);
		
		// create the action sequences and add the action wait
		ActionSequence actionSequence1 = new ActionSequence();
		actionSequence1.addAction(actionWait1);
		ActionSequence actionSequence2 = new ActionSequence();
		actionSequence2.addAction(actionWait2);
		ActionSequence actionSequence3 = new ActionSequence();
		actionSequence3.addAction(actionWait3);
		ActionSequence actionSequence4 = new ActionSequence();
		actionSequence4.addAction(actionWait4);

		// --------------

		ActionSequenceWrapper actionSequenceWrapper = new ActionSequenceWrapper();

		// adds at index 0 of internal array
		actionSequenceWrapper.setActionSequence(0, 0, actionSequence1);

		// adds at index 1 of internal array
		actionSequenceWrapper.setActionSequence(0, 1, actionSequence2);

		// adds at index 2 of internal array
		actionSequenceWrapper.setActionSequence(1, 0, actionSequence3);

		// adds at index 3 of internal array
		actionSequenceWrapper.setActionSequence(1, 1, actionSequence4);

		// test the thing
		// 0
		ActionWait actionWaitTest1 = (ActionWait) actionSequenceWrapper.getActionSequence0And0().getActions().get(0);
		assertEquals(0, actionWaitTest1.getWaitTime()); 
		// 1
		ActionWait actionWaitTest2 = (ActionWait) actionSequenceWrapper.getActionSequence0And1().getActions().get(0);
		assertEquals(1,actionWaitTest2.getWaitTime()); 
		// 2
		ActionWait actionWaitTest3 = (ActionWait) actionSequenceWrapper.getActionSequence1And0().getActions().get(0);
		assertEquals(2,actionWaitTest3.getWaitTime()); 
		// 3
		ActionWait actionWaitTest4 = (ActionWait) actionSequenceWrapper.getActionSequence1And1().getActions().get(0);
		assertEquals(3, actionWaitTest4.getWaitTime()); 
	}

}
