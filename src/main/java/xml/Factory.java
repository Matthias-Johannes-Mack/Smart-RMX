package xml;

import action.*;
import action.actions.*;
import action.actionSequence.ActionSequence;
import action.actions.actionMessages.ActionMessageBit;
import action.actions.actionMessages.ActionMessageBitToggle;
import action.actions.actionMessages.ActionMessageByte;
import action.actions.actionMessages.ActionMessageByteIncDecRement;
import matrix.byteMatrix.ByteCondition;
import matrix.byteMatrix.ByteMatrix;
import matrix.byteMatrix.ByteRule;
import matrix.bitMatrix.BitMatrix;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Factory class for creating the actions matrix and filling the matrix
 */
public class Factory {
	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of Factory
	 */
	private static Factory instance = null;

	/**
	 * private constructor to prevent instantiation
	 */
	private Factory() {
		bitRules = new ArrayList<>();
		actionDepot = ActionDepot.getActionDepot();
		byteRules = new ArrayList<>();
	}

	/**
	 * Returns singleton Factory instance
	 *
	 * @return Factory Singleton instance
	 */
	public static synchronized Factory getFactory() {
		if (instance == null) {
			instance = new Factory();
		}

		return instance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * bit rules that have been read in from the xml, will be set by XML_read class
 	 */
	private ArrayList<BitRule> bitRules;

	/**
	 * action depot instance
	 */
	private ActionDepot actionDepot;

	/**
	 * byte rules that have been read in from the xml, will be set by XML_read class
	 */
	private ArrayList<ByteRule> byteRules;

	/**
	 * adds a byte rule to the factory
	 * @param conditionOne [Bus, Systemadress, Equal, NotEqual, Bigger, Smaller]
	 * @param conditionTwo [Bus, Systemadress, Equal, NotEqual, Bigger, Smaller]
	 * @param actions Arraylist containg the actions as XML_ActionWrapper
	 */
	protected void addByteRule(Integer[] conditionOne, Integer[] conditionTwo, ArrayList<XML_ActionWrapper> actions) {
		ActionSequence actionSequence = createActionSequence(actions);
		ByteCondition byteConditionOneObj = createCondition(conditionOne);
		ByteCondition byteConditionTwoObj = createCondition(conditionTwo);

		RulePrintUtil.printByteRule(byteConditionOneObj, byteConditionTwoObj, actions);
		byteRules.add(new ByteRule(byteConditionOneObj, byteConditionTwoObj, actionSequence));
	}

	/**
	 * adds a bit rule to the bit rules
	 * @param conditionsOne [Bus, SystemAddress, Bit]
	 * @param conditionsTwo [Bus, SystemAddress, Bit]
	 * @param actions ArrayList<XML_ActionWrapper>
	 */
	protected void addBitRule(Integer[] conditionsOne, Integer[] conditionsTwo, ArrayList<XML_ActionWrapper> actions) {
		RulePrintUtil.printBitRule(conditionsOne, conditionsTwo, actions);
		bitRules.add(new BitRule(conditionsOne, conditionsTwo, actions));
	}

	/**
	 * creates Action and saves it to the action depot
	 */
	public void createActionsAndMatrix() {
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
	 * creates a condition object for a byte Objecte
	 * @param conditionArray [Bus, Systemadress, Equals, NotEquals, Bigger, Smaller]
	 * @return Condition Object
	 */
	private ByteCondition createCondition(Integer[] conditionArray) {
		//only need the bus and Systemadress for the constructor
		ByteCondition cond = new ByteCondition(Arrays.copyOfRange(conditionArray, 0, 2));

		for(int i = 2; i <= 5; i++) {
			if(conditionArray[i] != null) {
				switch(i) {
					case 2:
						cond.setEqual(conditionArray[i]);
						break;
					case 3:
						cond.setNotEqual(conditionArray[i]);
						break;
					case 4:
						cond.setBigger(conditionArray[i]);
						break;
					case 5:
						cond.setSmaller(conditionArray[i]);
						break;
				}
			}
		}

		return cond;
	}

	/**
	 * creates an action sequence for a given list of XML_ActionWrapper to make sure if an action occues multiple times in
	 * the xml document only one action object is stored in the application to reduce memory
	 *
	 * @param actions List of XML_ActionWrappers to be converted into an action sequence
	 * @return action sequence containing the action elements to be added to the matrix
	 */
	private ActionSequence createActionSequence(ArrayList<XML_ActionWrapper> actions) {
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
}
