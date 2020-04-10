package byteMatrix;

import java.util.ArrayList;

/**
 * Class that holds the ByteRule for an field in the matrix
 */
public class ByteRuleWrapper {
	/**
	 * 
	 */
	private ArrayList<ByteRule> ruleWrapper;

	/**
	 * Constructor
	 */
	public ByteRuleWrapper() {
		ruleWrapper = new ArrayList<>();
	}

	/**
	 * Setter for the ByteRule
	 * 
	 * @param byteRule
	 */
	public void setRuleWrapper(ByteRule byteRule) {
		if (byteRule != null) {
			ruleWrapper.add(byteRule);
		}
	}

}
