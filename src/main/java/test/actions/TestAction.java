package test.actions;

import action.Action;
import action.ActionMessage;
import action.ActionSequence;
import action.ActionWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAction {

    public static void main(String[] args) {

        int[] message1 = new int[] {1,1,1};
        int[] message2 = new int[] {1,1,2};

        ActionMessage actionMessage1 = new ActionMessage(message1);
        ActionMessage actionMessage2 = new ActionMessage(message2);
        ActionMessage actionMessage3 = new ActionMessage(message2);

        ActionWait actionWait1 = new ActionWait(1000);
        ActionWait actionWait2 = new ActionWait(2000);
        ActionWait actionWait3 = new ActionWait(2000);

        List<Action> actionList = new ArrayList<>();
        actionList.add(actionMessage1);
        actionList.add(actionMessage2);
        actionList.add(actionMessage3);
        actionList.add(actionWait1);
        actionList.add(actionWait2);
        // actionList.add(actionWait3); not added to the list

        //----------- testing

        // equals
        System.out.println(actionWait1.equals(actionWait2)); // false
        System.out.println(actionWait2.equals(actionWait3)); // true

        System.out.println(actionMessage1.equals(actionMessage2)); // false
        System.out.println(actionMessage2.equals(actionMessage3)); // true

        System.out.println(actionMessage1.equals(actionWait1)); // false
        System.out.println(actionWait1.equals(actionMessage1)); // false

        System.out.println(actionList.contains(actionWait3)); // true da ja zeit (2000) schon drin
        System.out.println(actionList.contains(actionMessage1)); // true
    }
    
}
