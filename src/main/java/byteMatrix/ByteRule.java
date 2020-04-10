package byteMatrix;

import java.util.ArrayList;
import java.util.Arrays;

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
	 * @param conditionOneAdress
	 * @param conditionOneValue
	 * @param conditionTwoAdress
	 * @param conditionTwoValue
	 * @param actions
	 */
	public ByteRule(Integer[] conditionOneAdress, Integer[] conditionOneValue, Integer[] conditionTwoAdress,
			Integer[] conditionTwoValue, ArrayList<Integer[]> actions) {
		// compare indexes, the smaller one is ConditionOneAdress, the bigger one is
		// condionTwoAdress
		if (conditionOneAdress[0] < conditionTwoAdress[0]) {
			this.conditionOneAdress = conditionOneAdress;
			this.conditionOneValue = conditionOneValue;
			this.conditionTwoAdress = conditionTwoAdress;
			this.conditionTwoValue = conditionTwoValue;
			// compare indexes, the bigger one is ConditionOne
		} else if (conditionOneAdress[0] > conditionTwoAdress[0]) {
			this.conditionOneAdress = conditionTwoAdress;
			this.conditionOneValue = conditionTwoValue;
			this.conditionTwoAdress = conditionOneAdress;
			this.conditionTwoValue = conditionOneValue;
		} else {
			// 
			if (conditionOneAdress[1] < conditionTwoAdress[1]) {
				this.conditionOneAdress = conditionOneAdress;
				this.conditionOneValue = conditionOneValue;
				this.conditionTwoAdress = conditionTwoAdress;
				this.conditionTwoValue = conditionTwoValue;
			} else if (conditionOneAdress[1] >= conditionTwoAdress[1]) {
				this.conditionOneAdress = conditionTwoAdress;
				this.conditionOneValue = conditionTwoValue;
				this.conditionTwoAdress = conditionOneAdress;
				this.conditionTwoValue = conditionOneValue;
			}
		}

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

	@Override
	/**
	 * Changed the equals method, that it returns true if both conditions are the
	 * same
	 */
	public boolean equals(Object obj) {
		// if same object
		if (this == obj) {
			return true;
		}

		// if null or not even same class
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ByteRule objByteRule = (ByteRule) obj;
		// match the condtionAdress of the first Conditon
		if (Arrays.equals(getConditionOneAdress(), objByteRule.getConditionOneAdress())) {
			// match the two Values of Conditions one
			if (Arrays.equals(getConditionOneValue(), objByteRule.getConditionOneValue())) {

			}
		}
	}

}
