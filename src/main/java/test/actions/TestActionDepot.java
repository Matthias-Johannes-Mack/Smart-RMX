package test.actions;

import action.ActionDepot;
import action.ActionMessage;
import action.ActionWait;

public class TestActionDepot {

    public static void main(String[] args) {

        int[] message1 = new int[] {1,1,1};
        ActionMessage actionMessage1 = new ActionMessage(message1);
        ActionMessage actionMessage2 = new ActionMessage(message1);

        ActionWait actionWait1 = new ActionWait(1000);
        ActionWait actionWait2 = new ActionWait(1000);

        //--------------
        ActionDepot actionDepot = ActionDepot.getActionDepot();

        // == to compare the reference
        System.out.println(actionMessage1 == actionDepot.addAction(actionMessage1)); // true

        System.out.println(actionMessage2 == actionDepot.addAction(actionMessage2)); // false

        System.out.println(actionWait1 == actionDepot.addAction(actionWait1)); // true

        System.out.println(actionWait2 == actionDepot.addAction(actionWait2)); // false
    }

}
