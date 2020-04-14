package xml;

import action.*;
import byteMatrix.ByteMatrix;
import byteMatrix.ByteRule;
import byteMatrix.Condition;
import matrix.BitMatrix;
import java.util.ArrayList;
import java.util.Arrays;

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
	 * @param conditionOne [Bus, Systemadress, Equal, NotEqual, Bigger, Smaller] Integer Array
	 * @param actions Arraylist containg the action Integer Arrays
	 */
	protected static void addByteRule(Integer[] conditionOne, Integer[] conditionTwo, ArrayList actions) {
		ActionSequence actionSequence = createActionSequence(actions);
		Condition conditionOneObj = createCondition(conditionOne);
		Condition conditionTwoObj = createCondition(conditionTwo);

		System.out.println("ConditionONe in addByteRule: " + Arrays.toString(conditionOne));
		System.out.println("ConditionONe in addByteRule: " + Arrays.toString(conditionTwo));

		byteRules.add(new ByteRule(conditionOneObj, conditionTwoObj, actionSequence));
	}

	/**
	 * creates a condition object
	 * @param conditionArray [Bus, Systemadress, Equals, NotEquals, Bigger, Smaller]
	 * @return Condition Object
	 */
	private static Condition createCondition(Integer[] conditionArray) {
		System.out.println("ConditionArray in createCondition: " + Arrays.toString(conditionArray));

		//only need the bus and Systemadress for the constructor
		Condition cond = new Condition(Arrays.copyOfRange(conditionArray, 0, 2));

		if(conditionArray[2] != null) {
			cond.setEqual(conditionArray[2]);
			System.out.println("EQUAL");
		}

		if(conditionArray[3] != null) {
			cond.setNotEqual(conditionArray[3]);
			System.out.println("NOTEQUAL");
		}

		if(conditionArray[4] != null) {
			cond.setBigger(conditionArray[4]);
			System.out.println("BIGGER");
		}

		if(conditionArray[5] != null) {
			cond.setSmaller(conditionArray[5]);
			System.out.println("SMALLER");
		}

		return cond;
	}

	/**
	 * adds a bit rule to the bit rules
	 * @param conditionsOne [Bus, SystemAddress, Bit]
	 * @param conditionsTwo [Bus, SystemAddress, Bit]
	 * @param actions ArrayList<XML_ActionWrapper>
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
			ActionSequence actionSeq = createActionSequence(bitRule.getActions());

			BitMatrix.getMatrix().addAction(bitRule.getConditionOne(), bitRule.getConditionTwo(), actionSeq);
		}

		//adds rules to byte matrix
		for(ByteRule byteRule: byteRules) {
			ByteMatrix.getMatrix().addByteRule(byteRule);
		}
	}

	/**
	 * creates an action sequence for a given list of XML_ActionWrapper
	 *
	 * @param actions List of XML_ActionWrappers to be converted into an action sequence
	 * @return action sequence containing the action elements to be added to the matrix
	 */
	private static ActionSequence createActionSequence(ArrayList<XML_ActionWrapper> actions) {
		ActionSequence actionSeq = new ActionSequence();

		for (XML_ActionWrapper action : actions) {
			switch (action.getType()) {
				case WAIT:
					// create wait action and save it to action depot with id
					ActionWait waitAction = new ActionWait(action.getActionArray()[0]);
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(waitAction));
					break;
				case BITMESSAGE:
					ActionMessageBit messageBit = new ActionMessageBit(action.getActionArray());
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(messageBit));
					break;
				case BYTEMESSAGE:
					ActionMessageByte messageByte = new ActionMessageByte(action.getActionArray());
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(messageByte));
					break;
				case DECREMENT:
				case INCREMENT:
					ActionMessageByteIncDecRement messageByteDecrement = new ActionMessageByteIncDecRement(action.getActionArray());
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(messageByteDecrement));
					break;
				case BITTOGGLE:
					ActionMessageBitToggle messageBitToggle = new ActionMessageBitToggle(action.getActionArray());
					// only add action to actionDepot if it doesnt exists already
					actionSeq.addAction(actionDepot.addAction(messageBitToggle));
					break;
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
