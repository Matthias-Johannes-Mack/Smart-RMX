package xml;

import java.util.ArrayList;

/**
 * Rule object for storing the conditions and actions until the matrix is created
 */
public class Rule {
    /**
     * Integer Array for first Conditions [Bus, SystemAddress, Bit, Bitvalue]
     */
    private Integer[] conditionOne;
    /**
     * Integer Array for second Conditions [Bus, SystemAddress, Bit, Bitvalue]
     */
    private Integer[] conditionTwo;
    /**
     * List containing Integer Arrays for each Action [Bus, SystemAddress, Bit, Bitvalue] and Arrays for the Wait operation [time in ms]
     */
    private ArrayList<Integer[]> actions;

    /**
     * Constructor
     * @param conditionOne first Condition
     * @param conditionTwo second Condition
     * @param actions list containing actions
     */
    public Rule(Integer[] conditionOne, Integer[] conditionTwo, ArrayList<Integer[]> actions) {
        this.conditionOne = conditionOne;
        this.conditionTwo = conditionTwo;
        this.actions = actions;
    }

    /**
     * getter for actions
     * @return ArrayList containing actions
     */
    public ArrayList<Integer[]> getActions() {
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
