package matrix.byteMatrix;

import action.actionSequence.ActionSequence;
import matrix.factory.ByteRule;

import java.util.ArrayList;

/**
 * Class that holds the ByteRule for an field in the byte matrix
 */
public class ByteRuleWrapper {
	/**
	 * holds the ByteRules
	 */
	private ArrayList<ByteRule> byteRuleList;

	/**
	 * Constructor
	 */
	public ByteRuleWrapper() {
		byteRuleList = new ArrayList<>();
	}

	/**
	 * Checks if the ByteRuleWrapper has a ActionSequence for the given byteValues
	 *
	 * @param byteValueSmall byte Value of the byte with the smaller byteIndex in the matrix
	 * @param byteValueBig byte Value of the byte with the bigger byteIndex in the matrix
	 * @return the ActionSequence if a Rule has been defined with the given values, null otherwise
	 */
	public ActionSequence getActionSequenceByState(int byteValueSmall, int byteValueBig) {

		ActionSequence result = null;

		for (ByteRule rule: byteRuleList) {

			if(rule.check(byteValueSmall, byteValueBig)) {
				// condition is true
				result = rule.getActionSequence();
				break;
			}

		}

		return result;
	}

	/**
	 * adds the given rule to the wrapper, makes sure there is only one rule for a specific byte combination /value pair
	 * @param rule rule to add
	 */
	public void addByteRule(ByteRule rule) {
		if(!byteRuleList.contains(rule)) {
			this.byteRuleList.add(rule);
		}
	}

}
