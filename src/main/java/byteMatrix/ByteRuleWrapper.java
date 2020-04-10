package byteMatrix;

import action.ActionSequence;

import java.util.ArrayList;

/**
 * Class that holds the ByteRule for an field in the matrix
 */
public class ByteRuleWrapper {
	/**
	 * 
	 */
	private ArrayList<ByteRule> byteRuleList;

	/**
	 * Constructor
	 */
	public ByteRuleWrapper() {
		byteRuleList = new ArrayList<>();
	}

	/**
	 * Setter for the ByteRule
	 * 
	 * @param byteRule
	 */
	public void setByteRuleList(ByteRule byteRule) {
		if (byteRule != null) {
			byteRuleList.add(byteRule);
		}
	}


	//TODO später in implementiertung drauf achten in row schleife bin ich immer größer, in column schleife bin ich
	// immer kleiner

	/**
	 * Checks if the ByteRuleWrapper has a ActionSequence for the given byteValues
	 *
	 * @param byteValueSmall
	 * @param byteValueBig
	 * @return the ActionSequence if a Rule has been defined with the given values, null otherwise
	 */
	public ActionSequence getActionSequenceByState(Integer[] byteValueSmall, Integer[] byteValueBig) {

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

}
