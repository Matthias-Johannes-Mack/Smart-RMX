package xml;

import action.*;
import byteMatrix.ByteMatrix;
import byteMatrix.ByteRule;
import matrix.BitMatrix;
import java.util.ArrayList;

/**
 * Factory class for creating the actions matrix and filling the matrix
 */
public class Factory {
	/**
	 * bit rules that have been read in from the xml, will be set by XML_read class
 	 */
	static private ArrayList<BitRule> bitRules = new ArrayList<>();

	/**
	 * action depot instance
	 */
	static private ActionDepot actionDepot = ActionDepot.getActionDepot();

	/**
	 * byte rules that have been read in from the xml, will be set by XML_read class
	 */
	static private ArrayList<ByteRule> byteRules = new ArrayList<>();

	/**
	 * adds a byte rule to the factory
	 * @param conditionOneAdress [Bus, Systemadress] Integer Array
	 * @param conditionOneValue [0,1, ..., 7] Integer Array of length 8 representing the byte
	 * @param conditionTwoAdress [Bus, Systemadress] Integer Array
	 * @param conditionTwoValue [0,1, ..., 7] Integer Array of length 8 representing the byte
	 * @param actions Arraylist containg the action Integer Arrays
	 */
	protected static void addByteRule(Integer[] conditionOneAdress, Integer[] conditionOneValue, Integer[] conditionTwoAdress, Integer[] conditionTwoValue, ArrayList actions) {
		ActionSequence actionSequence = createActionSequence(actions);
		byteRules.add(new ByteRule(conditionOneAdress, conditionOneValue, conditionTwoAdress, conditionTwoValue, actionSequence));
	}

	/**
	 * adds a rule to the rule list
	 * TODO
	 * @param
	 */
	protected static void addBitRule(Integer[] conditionsOne, Integer[] conditionsTwo, ArrayList actions) {
		bitRules.add(new BitRule(conditionsOne, conditionsTwo, actions));
	}

	/**
	 * getter for the list containing the rules that have been read in from the rule
	 * 
	 * @return ArrayList containing the rules
	 */
	protected static ArrayList<BitRule> getBitRules() {
		return bitRules;
	}

	/**
	 * creates Action and saves it to the action depot
	 */
	public static void createActionsAndMatrix() {
		//adds rules to byte matrix
		for (BitRule bitRule : bitRules) {
			ArrayList<Integer[]> actions = bitRule.getActions();
			ActionSequence actionSeq = createActionSequence(actions);

			BitMatrix.getMatrix().addAction(bitRule.getConditionOne(), bitRule.getConditionTwo(), actionSeq);
		}

		//adds rules to byte matrix
		for(ByteRule byteRule: byteRules) {
			ByteMatrix.getMatrix().addByteRule(byteRule);
		}
	}

	/**
	 * creates an action sequence for a given list of actions
	 *
	 * Integer Array for ActionMessageBit: [Bus][Systemadress][Bit][BitValue]
	 * Integer Array for ActionMessageByte: [Bus][Systemadress][Bit][ByteValue]
	 *  Integer Array for waitAction: [Wait time in ms]
	 *
	 * @param actions actions to be converted into an action sequence
	 * @return action sequence
	 */
	private static ActionSequence createActionSequence(ArrayList<Integer[]> actions) {
		ActionSequence actionSeq = new ActionSequence();

		for (Integer[] action : actions) {
			// wait action
			if (action.length == 1) {
				// create wait action and save it to action depot with id
				ActionWait waitAction = new ActionWait(action[0]);
				// only add action to actionDepot if it doesnt exists already
				actionSeq.addAction(actionDepot.addAction(waitAction));
			} else if(action.length == 4) {
				ActionMessageBit messageBit = new ActionMessageBit(parseIntegerToIntArr(action));
				// only add action to actionDepot if it doesnt exists already
				actionSeq.addAction(actionDepot.addAction(messageBit));
			} else if(action.length ==3) {
				ActionMessageByte messageByte = new ActionMessageByte(parseIntegerToIntArr(action));
				// only add action to actionDepot if it doesnt exists already
				actionSeq.addAction(actionDepot.addAction(messageByte));
			}
		}
		return actionSeq;
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
