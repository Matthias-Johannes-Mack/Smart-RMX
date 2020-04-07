package action;

import java.util.Arrays;
import java.util.List;

public class TestActions {

    public static void main(String[] args) {

        int[] message1 = new int[] {1,1,1};
        int[] message2 = new int[] {1,1,2};
        int[] message3 = new int[] {1,1,3};
        int[] message4 = new int[] {1,1,4};

        ActionMessage action1 = new ActionMessage(message1);
        ActionMessage action2 = new ActionMessage(message2);
        ActionMessage action3 = new ActionMessage(message3);
        ActionMessage action4 = new ActionMessage(message4);

        ActionSequence actionSequence = new ActionSequence();

        actionSequence.addAction(action1);
        actionSequence.addAction(action2);
        actionSequence.addAction(action3);
        actionSequence.addAction(action4);

        List<Action> actionList = actionSequence.getActions();
        System.out.println(actionList);

        for(int i = 0; i < actionSequence.getActionCount(); i++) {
            ActionMessage ac = (ActionMessage) actionSequence.getAction(i);

            // if wait
            // neuer thread i
            // break;

            System.out.println(Arrays.toString(ac.getActionMesssage()));
        }

    }
    
}
