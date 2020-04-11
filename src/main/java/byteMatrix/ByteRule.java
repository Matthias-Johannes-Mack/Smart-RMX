package byteMatrix;

import action.ActionSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
	 * actionSequence that is triggered if the Rule is true
	 */
	private ActionSequence actionSequence;

	/**
	 * Constructor
	 *
	 * internally saves the condition with the smaller bus and systemadress as conditionOne and the bigger one as
	 * conditionTwo
	 * 
	 * @param conditionOneAdress
	 * @param conditionOneValue
	 * @param conditionTwoAdress
	 * @param conditionTwoValue
	 * @param actionSequence
	 */
	public ByteRule(Integer[] conditionOneAdress, Integer[] conditionOneValue, Integer[] conditionTwoAdress,
			Integer[] conditionTwoValue, ActionSequence actionSequence) {

		if (conditionOneAdress[0] < conditionTwoAdress[0]) {
			// ConditionOne is smaller
			this.conditionOneAdress = conditionOneAdress;
			this.conditionOneValue = conditionOneValue;
			this.conditionTwoAdress = conditionTwoAdress;
			this.conditionTwoValue = conditionTwoValue;
		} else if (conditionOneAdress[0] > conditionTwoAdress[0]) {
			// ConditionTwo is smaller
			this.conditionOneAdress = conditionTwoAdress;
			this.conditionOneValue = conditionTwoValue;
			this.conditionTwoAdress = conditionOneAdress;
			this.conditionTwoValue = conditionOneValue;
		} else {
			// both conditions are in the same bus
			if (conditionOneAdress[1] < conditionTwoAdress[1]) {
				// ConditoinOne is smaller
				this.conditionOneAdress = conditionOneAdress;
				this.conditionOneValue = conditionOneValue;
				this.conditionTwoAdress = conditionTwoAdress;
				this.conditionTwoValue = conditionTwoValue;
			} else if (conditionOneAdress[1] >= conditionTwoAdress[1]) {
				// conditionTwo is smaller or they are equal
				this.conditionOneAdress = conditionTwoAdress;
				this.conditionOneValue = conditionTwoValue;
				this.conditionTwoAdress = conditionOneAdress;
				this.conditionTwoValue = conditionOneValue;
			}
		}

		this.actionSequence = actionSequence;
	}

	/**
	 * checks if the Rule is true with the given conditions
	 * @param conditionOneValue
	 * @param conditionTwoValue
	 * @return
	 */
	public boolean check(Integer[] conditionOneValue, Integer[] conditionTwoValue){
		return (Arrays.equals(this.conditionOneValue, conditionOneValue) &&
				Arrays.equals(this.conditionTwoValue, conditionTwoValue));
	}

	public ActionSequence getActionSequence() {
		return actionSequence;
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

	// to make sure there is only one rule for each byte state and combination
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ByteRule byteRule = (ByteRule) o;
		return Arrays.equals(conditionOneAdress, byteRule.conditionOneAdress) &&
				Arrays.equals(conditionOneValue, byteRule.conditionOneValue) &&
				Arrays.equals(conditionTwoAdress, byteRule.conditionTwoAdress) &&
				Arrays.equals(conditionTwoValue, byteRule.conditionTwoValue) &&
				Objects.equals(actionSequence, byteRule.actionSequence);
	}
}
