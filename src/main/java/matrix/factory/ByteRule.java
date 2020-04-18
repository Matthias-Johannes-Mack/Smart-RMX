package matrix.factory;

import action.actionSequence.ActionSequence;
import matrix.byteMatrix.ByteCondition;

/**
 * Class for a byte Rule
 *
 * @author Team RMX
 */
public class ByteRule {

	/**
	 * first condition of the byte rule
	 */
	ByteCondition byteConditionOne;

	/**
	 * second condition of the byte rule
	 */
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
	 * @param byteConditionOne first condition
	 * @param byteConditionTwo second condition
	 * @param actionSequence actionSequence containing the actions
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
	 * checks the first and second condition of the rule to see if all the conditions for the rule are true and the rule fires
	 *
	 * @param conditionOneValue value of the first byte of the matrix field, should be the byte with the smaller byteIndex
	 * @param conditionTwoValue value of the second byte of the matrix field, should be the byte with the bigger byteIndex
	 * @return true if both conditions are true, false otherwise
	 */
	public boolean check(int conditionOneValue, int conditionTwoValue){
		return (byteConditionOne.checkCondition(conditionOneValue) && byteConditionTwo.checkCondition(conditionTwoValue));
	}

	/**
	 * getter for the ActionSequence of the byte rule
	 * @return ActionSequence Object containing the Actions
	 */
	public ActionSequence getActionSequence() {
		return actionSequence;
	}

	/**
	 * getter for the first condition of the byte rule
	 * @return ByteCondition Object
	 */
	public ByteCondition getByteConditionOne() {
		return byteConditionOne;
	}

	/**
	 * getter for the second condition of the byte rule
	 * @return ByteCondition Object
	 */
	public ByteCondition getByteConditionTwo() {
		return byteConditionTwo;
	}

	/**
	 * to make sure there is only one rule for each byte state and combination
	 * two ByteRules are equal if both objects are byte rules and have the same conditions
	 *
	 * @param o object that should be checked against to see if they are equal
	 * @return true if the byte rule objects are equal, false otherwise
	 */
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


}
