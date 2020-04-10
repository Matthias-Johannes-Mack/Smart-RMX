package byteMatrix;

import java.util.ArrayList;

/**
 * Class for a byte Rule
 *
 * @author Team RMX
 */
public class ByteRule {
	/**
	 * Integer Array for first Conditions [Bus, SystemAddress]
	 */
	private Integer[] conditionOneAdress;
	/**
	 * Integer Array for first Conditions [Bytevalue]
	 */
	private Integer[] conditionOneValue;

	/**
	 * Integer Array for second Conditions [Bus, SystemAddress, Bytevalue]
	 */
	private Integer[] conditionTwoAdress;
	/**
	 * Integer Array for second Conditions [Bytevalue]
	 */
	private Integer[] conditionTwoValue;
	/**
	 * List containing Integer Arrays for each Action [Bus, SystemAddress,
	 * Bytevalue] and Arrays for the Wait operation [time in ms]
	 */
	private ArrayList<Integer[]> actions;

	/**
	 * Constructor
	 * 
	 * @param conditionOne first Condition
	 * @param conditionTwo second Condition
	 * @param actions      list containing actions
	 */
	public ByteRule(Integer[] conditionOneAdress, Integer[] conditionOneValue, Integer[] conditionTwoAdress,
			Integer[] conditionTwoValue, ArrayList<Integer[]> actions) {
		this.conditionOneAdress = conditionOneAdress;
		this.conditionOneValue = conditionOneValue;
		this.conditionTwoAdress = conditionTwoAdress;
		this.conditionTwoValue = conditionTwoValue;
		this.actions = actions;
	}

	/**
	 * getter for actions
	 * 
	 * @return ArrayList containing actions
	 */
	public ArrayList<Integer[]> getActions() {
		return actions;
	}

	/**
	 * @return the conditionOneAdress
	 */
	public Integer[] getConditionOneAdress() {
		return conditionOneAdress;
	}

	/**
	 * @return the conditionOneValue
	 */
	public Integer[] getConditionOneValue() {
		return conditionOneValue;
	}

	/**
	 * @return the conditionTwoAdress
	 */
	public Integer[] getConditionTwoAdress() {
		return conditionTwoAdress;
	}

	/**
	 * @return the conditionTwoValue
	 */
	public Integer[] getConditionTwoValue() {
		return conditionTwoValue;
	}
}
