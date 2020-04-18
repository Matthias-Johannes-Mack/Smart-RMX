package matrix.factory;

import action.actionSequence.ActionSequence;
import matrix.byteMatrix.ByteCondition;

/**
 * Class for a byte Rule
 *
 * @author Team RMX
 */
public class ByteRule {

	ByteCondition byteConditionOne;

	ByteCondition byteConditionTwo;

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
	 * @param byteConditionOne
	 * @param byteConditionTwo
	 * @param actionSequence
	 */
	public ByteRule(ByteCondition byteConditionOne, ByteCondition byteConditionTwo, ActionSequence actionSequence) {

		int compareResult = byteConditionOne.compareTo(byteConditionTwo);


		if(compareResult <= 0) {
			// condition one is smaller or they are equal
			this.byteConditionOne = byteConditionOne;
			this.byteConditionTwo = byteConditionTwo;
		} else {
			// condition two is smaller
			this.byteConditionOne = byteConditionTwo;
			this.byteConditionTwo = byteConditionOne;
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

		return (byteConditionOne.checkCondition(conditionOneValue) && byteConditionTwo.checkCondition(conditionTwoValue));
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
		return byteConditionOne.equals(byteRule.byteConditionOne) && byteConditionTwo.equals(byteRule.byteConditionTwo);
	}

	public ByteCondition getByteConditionOne() {
		return byteConditionOne;
	}

	public ByteCondition getByteConditionTwo() {
		return byteConditionTwo;
	}
}
