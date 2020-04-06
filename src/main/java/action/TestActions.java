package action;

import java.util.Arrays;

public class TestActions {

    public static void main(String[] args) {

        int[] message = new int[] {1,1,1};
        int[] message2 = new int[] {1,1,2};

        ActionMessage action = new ActionMessage(message);
        ActionMessage action2 = new ActionMessage(message2);
        ActionMessage action3 = new ActionMessage(message);

        ActionWait actionWait = new ActionWait(100);
        ActionWait actionWait2 = new ActionWait(200);
        ActionWait actionWait3 = new ActionWait(100);

        ActionDepot actionDepot = ActionDepot.getActionDepot();

        System.out.println(actionDepot.addAction(action));
        System.out.println(actionDepot.addAction(actionWait));
        System.out.println(actionDepot.addAction(action3));
        System.out.println(actionDepot.addAction(actionWait3));
    	
    }
    
}
