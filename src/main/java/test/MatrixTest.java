package test;

import action.Action;
import action.ActionSequence;
import action.ActionWait;
import bus.BusDepot;
import matrix.Matrix;
import java.util.List;

public class MatrixTest {

	public static void main(String[] args) {

		BusDepot busDepot = BusDepot.getBusDepot();
		busDepot.updateBus(new byte[]{0x06, 0x01, 0x6f, 0x00});

		// ActionSequence that contains ActionWait with waitTime 1
		Action actionWait1 = new ActionWait(1);
		ActionSequence actionSequence1 = new ActionSequence();
		actionSequence1.addAction(actionWait1);

		Action actionWait2 = new ActionWait(0);
		ActionSequence actionSequence2 = new ActionSequence();
		actionSequence2.addAction(actionWait2);


		// [Bus][Systemadresse][Bit][BitValue]
		Integer[] conditionOne = new Integer[]{1,111,0,1};
		Integer[] conditionTwo = new Integer[]{1,111,0,1};

		Integer[] conditionThree = new Integer[]{1,111,0,0};
		Integer[] conditionFour = new Integer[]{1,111,0,0};

		Matrix matrix = Matrix.getMatrix();
		matrix.addAction(conditionOne, conditionTwo, actionSequence1); // sollte zu wrapper hinzugefügt werden an 1-1
		matrix.addAction(conditionThree, conditionFour, actionSequence2); // sollte zu wrapper hinzugefügt werden an 0-0


		List<ActionSequence> resultCheckAll = matrix.checkAllFields();
		for (ActionSequence actionSequence : resultCheckAll) {
			List<Action> actions = actionSequence.getActions();

			for (Action action: actions) {
				if(action instanceof ActionWait) {
					ActionWait waitaction = (ActionWait) action;
					System.err.println(waitaction.getWaitTime()); // 0
				}
			}
		}

		// format <0x06><RMX><ADRRMX><VALUE>
		byte[] message1 = new byte[]{6, 1, 111, 1};
		Integer[] changes1 = busDepot.getChangesAndUpdate(message1); // bit 0 wurde auf 1 gesetzt


		List<ActionSequence> resultCheck = matrix.check(message1[1], message1[2], changes1);
		for (ActionSequence actionSequence : resultCheck) {
			List<Action> actions = actionSequence.getActions();

			for (Action action: actions) {
				if(action instanceof ActionWait) {
					ActionWait waitaction = (ActionWait) action;
					System.err.println(waitaction.getWaitTime()); // 1
				}
			}
		}

	}

}
