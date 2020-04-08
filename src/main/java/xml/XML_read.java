package xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Class that reads a XML-File into a buffer
 *
 * @author Matthias Mack 3316380
 */
public class XML_read {
    private org.w3c.dom.Document xmlDoc;

    /**
     * Constructor
     *
     * @param xmlDoc xml Document
     */
    public XML_read(org.w3c.dom.Document xmlDoc) {
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
     * reads in the rules from the file and saves them to the Factory Class.
     * A rule consists of
     * - Integer Array for each of the two Conditions [Bus, SystemAddress, Bit]
     * - List containing Integer Arrays for each Action [Bus, SystemAddress, Bit, Bitvalue] and Arrays for the Wait operation [time in ms]
     *
     *
     * @throws IllegalArgumentException
     */
    private void readXML() throws IllegalArgumentException {
		/*
		contains NodeLists containing all of the child elements of a Rule Node. Each index is for a different Rule node
		 */
        ArrayList<NodeList> ruleNodeChildrenArrList = new ArrayList<>();

        if (xmlDoc == null) {
            throw new IllegalArgumentException("XML Document cannot be NULL");
        }

        xmlDoc.getDocumentElement().normalize();
        String rootElement = xmlDoc.getDocumentElement().getNodeName();
        if (!rootElement.equals("Ruleset")) {
            throw new IllegalArgumentException("Root Element of XML File must be <Ruleset>!");
        }

        //gets the rule child nodes
        for (Node node : iterable(xmlDoc.getElementsByTagName("Rule"))) {
            NodeList nodeList = node.getChildNodes();
            ruleNodeChildrenArrList.add(nodeList);
        }

        //iterate over every rule nodes children
        for (NodeList nodeList : ruleNodeChildrenArrList) {
            // contains the first condition
            Integer[] conditionsOne = new Integer[4];
            // contains the second condition
            Integer[] conditionsTwo = new Integer[4];
            // List containing all the actions for one rule als an integer Array
            ArrayList<Integer[]> actions = new ArrayList<>();

            // conditon counter to know which condition Array to save to
            int conditionCount = 0;
            //iterate over every child node of the rule
            for (Node ruleNodeChild : iterable(nodeList)) {
                if (ruleNodeChild.getNodeName().equals("#text")) {
                    continue;
                }
                if (ruleNodeChild.getNodeName().equals("Condition")) {
                    conditionCount++;
                    for (Node conditionNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (conditionNodeChild.getNodeName().equals("#text")) {
                            continue;
                        }
                        //TODO bit value adden in processConditionAndActionChildNodes auslagern da in action und condition jetzt drin

                        if (conditionCount == 1) {
                            processConditionAndActionChildNodes(conditionsOne, conditionNodeChild);
                            if (conditionNodeChild.getNodeName().equals("BitValue")) {
                                conditionsOne[3] = Integer.parseInt(conditionNodeChild.getTextContent());
                            }
                        } else if (conditionCount == 2) {
                            processConditionAndActionChildNodes(conditionsTwo, conditionNodeChild);
                            if (conditionNodeChild.getNodeName().equals("BitValue")) {
                                conditionsTwo[3] = Integer.parseInt(conditionNodeChild.getTextContent());
                            }
                        }


                    }
                } // end of if equals Condition

                if (ruleNodeChild.getNodeName().equals("Action")) {
                    Integer[] action = new Integer[4];

                    for (Node actionNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (actionNodeChild.getNodeName().equals("#text")) {
                            continue;
                        }

                        processConditionAndActionChildNodes(action, actionNodeChild);

                        if (actionNodeChild.getNodeName().equals("BitValue")) {
                            action[3] = Integer.parseInt(actionNodeChild.getTextContent());
                        }
                    }
                    actions.add(action);
                }

                // for the wait add a IntegerArray containing only the wait time
                if (ruleNodeChild.getNodeName().equals("Wait")) {
                    Integer[] wait = new Integer[1];
                    wait[0] = Integer.parseInt(ruleNodeChild.getTextContent());
                    actions.add(wait);
                }
            }
            /*
              Iterating over one rule block done, add conditions and actions to new rule
             */
            Factory.addRule(new Rule(conditionsOne, conditionsTwo, actions));
        }
    }

    /**
     * helper method for readXML
     * processes the Bus, SystemAdress and Bit node of the Action and Condition nodes and puts them in the given array
     * @param targetArray array to which the node values should be written
     * @param node node whose content should be written to the array
     */
    private void processConditionAndActionChildNodes(Integer[] targetArray, Node node) {
        switch (node.getNodeName()) {
            case "Bus":
                targetArray[0] = Integer.parseInt(node.getTextContent());
                break;
            case "SystemAddress":
                targetArray[1] = Integer.parseInt(node.getTextContent());
                break;
            case "Bit":
                targetArray[2] = Integer.parseInt(node.getTextContent());
                break;
        }
    }


    //TODO Delete
    public void test() {
        System.out.println("Test:");
        System.out.println("Number of Rules " + Factory.getRules().size());
        for (Rule rule : Factory.getRules()) {
            System.out.println("----Rule----");
            System.out.println("Condition1: "+Arrays.toString(rule.getConditionOne()));
            System.out.println("Condition2:"+Arrays.toString(rule.getConditionOne()));
            for (Integer[] action : rule.getActions()) {
                System.out.println("Action: " + Arrays.toString(action));
            }
        }
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
