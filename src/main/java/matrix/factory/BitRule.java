package matrix.factory;

import xml.XML_ActionWrapper;

import java.util.ArrayList;

/**
 * Rule object for storing the bit conditions and actions until the matrix is created
 */
public class BitRule {
    /**
     * Integer Array for first Conditions [Bus, SystemAddress, Bit, Bitvalue]
     */
    private Integer[] conditionOne;
    /**
     * Integer Array for second Conditions [Bus, SystemAddress, Bit, Bitvalue]
     */
    private Integer[] conditionTwo;
    /**
     * List containing Integer Arrays for each Actions and Wait Actions
     */
    private ArrayList<XML_ActionWrapper> actions;

    /**
     * Constructor
     * @param conditionOne first Condition [Bus, SystemAddress, Bit]
     * @param conditionTwo second Condition [Bus, SystemAddress, Bit]
     * @param actions list containing XML_ActionsWrapper
     */
    public BitRule(Integer[] conditionOne, Integer[] conditionTwo, ArrayList<XML_ActionWrapper> actions) {
        this.conditionOne = conditionOne;
        this.conditionTwo = conditionTwo;
        this.actions = actions;
    }

    /**
     * getter for actions
     * @return ArrayList containing actions
     */
    public ArrayList<XML_ActionWrapper> getActions() {
        return actions;
    }

    /**
     * getter fot first condition
     * @return first condition
     */
    public Integer[] getConditionOne() {
        return conditionOne;
    }

    /**
     * getter for second condition
     * @return second condition
     */
    public Integer[] getConditionTwo() {
        return conditionTwo;
    }
}
