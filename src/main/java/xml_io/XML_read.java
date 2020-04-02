package xml_io;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.util.*;

/**
 * Class that reads a XML-File into a buffer
 *
 * @author Matthias Mack 3316380
 */
public class XML_read {
	private int ID;
	private org.w3c.dom.Document xmlDoc;


	/**
	 * Constructor
	 *
	 * @param xmlDoc xml Document
	 */
	public XML_read(org.w3c.dom.Document xmlDoc) {
		this.ID = 1;
		if (xmlDoc != null) {
			this.xmlDoc = xmlDoc;
		}

		try {
			readXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the Values of the xml an Creates Condition and Action Integer Arrays
	 * for the rules: [Train Number, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10,
	 * F11, F12, F13, F14, F15, F16, Speed, Direction] F0 -F16 can have values 0 or
	 * 1, 0 = button not pressed; 1 = button pressed Direction can have the values 0
	 * or 1: 0 = forwards; 1 = backwards
	 * <p>
	 * It creates for every Rule: ArrayList conditions including Integer Arrays with
	 * the conditions of the rule, one for every train number in the rule ArrayList
	 * actions including Integer Arrays with the actions of the rule, one for every
	 * train number in the rule
	 * <p>
	 * Example: ----Rule---- Conditions: [127, null, 1, 0, null, null, null, null,
	 * null, null, null, null, null, null, null, null, null, null, null, null]
	 * Conditions: [1, null, 1, 0, null, null, null, null, null, null, null, null,
	 * null, null, null, null, null, null, null, null] Actions: [127, null, null,
	 * null, 0, null, null, null, null, null, null, null, null, null, null, null,
	 * null, null, null, null] Actions: [1, null, null, null, 0, null, null, null,
	 * null, null, null, null, null, null, null, null, null, null, null, null]
	 *
	 * @throws IllegalArgumentException if XML File is null
	 */
	private void readXML() throws IllegalArgumentException {
		/*
		 * contains NodeLists containing all of the child elements of a Rule Node. Each
		 * index is for a different Rule node
		 */
		ArrayList<NodeList> ruleNodeChildrenArrList = new ArrayList<>();

		final List<String> fValues = Arrays.asList("F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10",
				"F11", "F12", "F13", "F14", "F15", "F16");

		/*
		 * size of the array that is created for the rules condition and actions [Train
		 * Number, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15,
		 * F16, Speed, Direction]
		 */
		final int ARRAYSIZE = 20;

		if (xmlDoc == null) {
			throw new IllegalArgumentException("XML Document cannot be NULL");
		}

		xmlDoc.getDocumentElement().normalize();
		String rootElement = xmlDoc.getDocumentElement().getNodeName();
		if (!rootElement.equals("Ruleset")) {
			throw new IllegalArgumentException("Root Element of XML File must be <Ruleset>!");
		}

		// gets the rule child nodes
		for (Node node : iterable(xmlDoc.getElementsByTagName("Rule"))) {
			NodeList nodeList = node.getChildNodes();
			ruleNodeChildrenArrList.add(nodeList);
		}

		// iterate over every rule nodes children
		for (NodeList nodeList : ruleNodeChildrenArrList) {
			ArrayList<Integer[]> conditions = new ArrayList<>();
			ArrayList<Integer[]> actions = new ArrayList<>();
			ArrayList<Boolean> flags = new ArrayList<>();

			// ID of the train the condition or action refers to
			int trainID = 0;
			// iterate over every child node of the rule
			for (Node ruleNodeChild : iterable(nodeList)) {
				if (ruleNodeChild.getNodeName().equals("#text")) {
					continue;
				}
				if (ruleNodeChild.getNodeName().equals("TrainID")) {
					for (Node trainIdChildNode : iterable(ruleNodeChild.getChildNodes())) {
						if (trainIdChildNode.getNodeName().equals("#text")) {
							continue;
						}
						if (trainIdChildNode.getNodeName().equals("ID")) {
							trainID = Integer.parseInt(trainIdChildNode.getTextContent());
						}
						if (trainIdChildNode.getNodeName().equals("Condition")) {
							readInConditionActionNodes(fValues, ARRAYSIZE, conditions, trainID, trainIdChildNode);
							// set the flag to false
							flags.add(false);
						}

						if (trainIdChildNode.getNodeName().equals("Action")) {
							readInConditionActionNodes(fValues, ARRAYSIZE, actions, trainID, trainIdChildNode);
						}

					}

				}

			}


		}
	}

	/**
	 * Helper method for readXML Iterate over the TrainID child nodes. In this case
	 * mainly for Iterating over condition and action nodes
	 *
	 * @param fValues                values of the FButtons
	 * @param ARRAYSIZE              Size of the Array in which solutions should be
	 *                               saved
	 * @param conditionsOrActionList conditions or actions: Arraylist to which to
	 *                               which the found values and resulting Integer
	 *                               Array should be added
	 * @param trainID                current ID of the train to which the condi
	 * @param trainIdChildNode       Node over whose childnodes should be iterated
	 */
	private void readInConditionActionNodes(List<String> fValues, int ARRAYSIZE,
			ArrayList<Integer[]> conditionsOrActionList, int trainID, Node trainIdChildNode) {
		Integer[] conditionOrActionArray = new Integer[ARRAYSIZE];
		// first index should be trainID
		conditionOrActionArray[0] = trainID;
		for (Node conditionChildNode : iterable(trainIdChildNode.getChildNodes())) {
			if (conditionChildNode.getNodeName().equals("#text")) {
				continue;
			}
			String conditionChildNodeName = conditionChildNode.getNodeName();
			// if the found element is a F1, F2,... button
			if (fValues.contains(conditionChildNodeName)) {
				int number = Integer.parseInt(conditionChildNodeName.substring(1)) + 1;
				if (conditionChildNode.getTextContent().equals("true")) {
					conditionOrActionArray[number] = 1;
				} else if (conditionChildNode.getTextContent().equals("false")) {
					conditionOrActionArray[number] = 0;
				}
			} else {
				switch (conditionChildNodeName) {
				case "Speed":
					conditionOrActionArray[18] = Integer.parseInt(conditionChildNode.getTextContent());
					break;
				case "Direction":
					conditionOrActionArray[19] = Integer.parseInt(conditionChildNode.getTextContent());
					break;
				}
			}
		}
		conditionsOrActionList.add(conditionOrActionArray);
	}

	

	/**
	 * Method to make nodelist iterable
	 *
	 * @param nodeList List of XML Nodes
	 * @return next Node
	 */
	public static Iterable<Node> iterable(final NodeList nodeList) {
		return () -> new Iterator<Node>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < nodeList.getLength();
			}

			@Override
			public Node next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return nodeList.item(index++);
			}
		};
	}
}
