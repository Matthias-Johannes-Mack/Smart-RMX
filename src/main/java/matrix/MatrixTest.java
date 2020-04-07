package matrix;


import action.Action;
import action.ActionSequence;
import action.ActionWait;
import bus.BusDepot;

import java.util.ArrayList;
import java.util.List;

public class MatrixTest {

	public static void main(String[] args) {

		ArrayList<Action> actionList = new ArrayList<>();
		actionList.add(new ActionWait(1000));

		ActionSequence actionSequence = new ActionSequence();
		// TODO add Actions from actionList for each

		// format <0x06><RMX><ADRRMX><VALUE>
		byte[] message = new byte[]{6, 1, 0, 1};

		BusDepot busDepot = BusDepot.getBusDepot();
		busDepot.updateBus(message); // creates bus 1 and setzt bit 1 von systemadresse 1

		Integer[] conditionOne = new Integer[]{1,0,0};
		Integer[] conditionTwo = new Integer[]{1,0,0};

		Matrix matrix = Matrix.getMatrix();
		matrix.addAction(conditionOne, conditionTwo, actionSequence);
		//System.out.println("matrix: " + matrix.matrix[0].getActions());


//		List<ActionSequence> result = matrix.check((byte)1,(byte)0, (byte)1);
//
//		for (ActionSequence actionSequence1: result) {
//			List<Action> actions = actionSequence.getActions();
//
//			for (Action action: actions) {
//				if(action instanceof ActionWait) {
//					ActionWait waitaction = (ActionWait) action;
//					System.out.println(waitaction.getWaitTime());
//				}
//			}
//		}


	}

}
