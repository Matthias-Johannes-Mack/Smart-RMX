package xml;

import action.ActionDepot;
import action.ActionMessage;
import action.ActionSequence;
import action.ActionWait;
import matrix.BitMatrix;
import java.util.ArrayList;

/**
 * Factory class for creating the actions matrix and filling the matrix
 */
public class Factory {
	/**
	 * rules that have been read in from the xml, will be set by XML_read class
 	 */
	static private ArrayList<Rule> rules = new ArrayList<>();

	/**
	 * adds a rule to the rule list
	 * 
	 * @param rule rule to add
	 */
	protected static void addRule(Rule rule) {
		rules.add(rule);
	}

	/**
	 * getter for the list containing the rules that have been read in from the rule
	 * 
	 * @return ArrayList containing the rules
	 */
	protected static ArrayList<Rule> getRules() {
		return rules;
	}

	/**
	 * creates Action and saves it to the action depot
	 */
	public static void createActionsAndMatrix() {

		// action depot
		ActionDepot actionDepot = ActionDepot.getActionDepot();

		for (Rule rule : rules) {
			ArrayList<Integer[]> actions = rule.getActions();
			ActionSequence actionSeq = new ActionSequence();

			for (Integer[] action : actions) {
				// wait action
				if (action.length == 1) {
					// create wait action and save it to action depot with id
					ActionWait waitAction = new ActionWait(action[0]);
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(waitAction));
				} else {
					ActionMessage messageAction = new ActionMessage(parseIntegerToIntArr(action));
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(messageAction));
				}
			}
			// Add rule to Matrix
			BitMatrix.getMatrix().addAction(rule.getConditionOne(), rule.getConditionTwo(), actionSeq);
		}
	}

	/**
	 * method, that convert a IntegerArray in intArray
	 * 
	 * @param integerArr
	 * @return
	 */
	private static int[] parseIntegerToIntArr(Integer[] integerArr) {
		int[] intArr = new int[integerArr.length];
		for (int i = 0; i < integerArr.length; i++) {
			intArr[i] = integerArr[i].intValue();
		}
		return intArr;
	}
}
