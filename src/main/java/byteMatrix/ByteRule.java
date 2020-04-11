package byteMatrix;

import action.ActionSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Class for a byte Rule
 *
 * @author Team RMX
 */
public class ByteRule {

	Condition conditionOne;

	Condition conditionTwo;

	/**
	 * actionSequence that is triggered if the Rule is true
	 */
	private ActionSequence actionSequence;

	/**
	 *
	 * Constructor
	 *
	 * internally saves the condition with the smaller bus and systemadress as conditionOne and the bigger one as
	 * conditionTwo
	 *
	 * @param conditinOne
	 * @param conditionTwo
	 * @param actionSequence
	 */
	public ByteRule(Condition conditionOne, Condition conditionTwo, ActionSequence actionSequence) {

		int compareResult = conditionOne.compareTo(conditionTwo);


		if(compareResult <= 0) {
			// condition one is smaller or they are equal
			this.conditionOne = conditionOne;
			this.conditionTwo = conditionTwo;
		} else {
			// condition two is smaller
			this.conditionOne = conditionTwo;
			this.conditionTwo = conditionOne;
		}

		this.actionSequence = actionSequence;
	}

	/**
	 * checks if the Rule is true with the given conditions
	 * @param conditionOneValue
	 * @param conditionTwoValue
	 * @return
	 */
	public boolean check(int conditionOneValue, int conditionTwoValue){
		System.out.println("Value one " + conditionOneValue + "Value two " + conditionTwoValue);

		boolean conditionOneResult = conditionOne.checkCondition(conditionOneValue);
		boolean conditionTwoResult = conditionTwo.checkCondition(conditionTwoValue);


		return (conditionOneResult && conditionTwoResult);
	}

	public ActionSequence getActionSequence() {
		return actionSequence;
	}

	// to make sure there is only one rule for each byte state and combination
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ByteRule byteRule = (ByteRule) o;
		return conditionOne.equals(byteRule.conditionOne) && conditionTwo.equals(byteRule.conditionTwo);
	}

	public Condition getConditionOne() {
		return conditionOne;
	}

	public Condition getConditionTwo() {
		return conditionTwo;
	}
}
