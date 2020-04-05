package xml;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Factory class for creating the actions matrix and filling the matrix
 */
public class Factory {
    //id that is given to the actions
    private static int actionID = 0;
    //rules that have been read in from the xml
    static private ArrayList<Rule> rules = new ArrayList<>();

    /**
     * adds a rule to the rule list
     * @param rule rule to add
     */
    protected static void addRule(Rule rule) {
        rules.add(rule);
    }

    /**
     * getter for the list containing the rules that have been read in from the rule
     * @return ArrayList containing the rules
     */
    protected static ArrayList<Rule> getRules() {
        return rules;
    }

    /**
     * creates Action and saves it to the action depot
     */
    protected static void createActionsAndMatrix(){
        createMatrix();

        for(Rule rule :rules) {
            ArrayList<Integer[]> actions = rule.getActions();
            ArrayList<Integer> actionIDsForRule = new ArrayList<>();

            for(Integer[] action : actions) {
                //wait action
                if(action.length == 1) {
                    //TODO create wait action and save it to action depot with id
                    actionIDsForRule.add(actionID);
                    actionID++;
                } else {
                //normal action
                    //todo create normal action with id
                    actionIDsForRule.add(actionID);
                    actionID++;
                }
            }
            //TODO add rule to Matrix
            addRuleToMatrix(actionIDsForRule, rule.getConditionOne(), rule.getConditionTwo());
        }
    }

    /**
     * creates the Matrix
     */
    private static void createMatrix() {
        //TODO
    }

    /**
     * Adds action ids of a rule to a field of the matrix
     * @param actionIDs action ids to add
     * @param conditionOne first condition of the rule
     * @param conditionTwo second condition of the rule
     */
    private static void addRuleToMatrix(ArrayList<Integer> actionIDs, Integer[] conditionOne, Integer[] conditionTwo) {
        // conditionOne = conditionTwo -> rule is on diagonal
        if(Arrays.equals(conditionOne, conditionTwo)) {
            //TODO
        } else {
            //TODO

        }
    }
}
