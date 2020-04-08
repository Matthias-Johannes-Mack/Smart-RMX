package xml;

import action.ActionSequenceWrapper;
import bus.BusDepot;
import matrix.Matrix;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Class that reads a XML-File into a buffer
 *
 * @author Matthias Mack 3316380
 */
public class XML_read {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of XML_read
     */
    private static XML_read instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private XML_read() {

    }

    /**
     * Returns singleton Matrix instance
     *
     * @return Matrix Singleton instance
     */
    public static synchronized XML_read getXML_read() {
        if (instance == null) {
            instance = new XML_read();
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________


    /**
     * xml document that is read in
     */
    private org.w3c.dom.Document xmlDoc;

    /**
     * reads and processes the given xml document
     *
     * @param xmlDoc xml Document
     */
    public void processXMLDocument(org.w3c.dom.Document xmlDoc) {
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
     */
    private void readXML() {
		/**
		contains NodeLists containing all of the child elements of a Rule Node. Each index is for a different Rule node
		 */
        ArrayList<NodeList> ruleNodeChildrenArrList = new ArrayList<>();

        if (xmlDoc == null) {
            throw new IllegalArgumentException("XML Document cannot be NULL");
        }

        xmlDoc.getDocumentElement().normalize();


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

            // condition counter to know which condition Array to save to
            int conditionCount = 0;
            //iterate over every child node of the rule
            for (Node ruleNodeChild : iterable(nodeList)) {
                if (ruleNodeChild.getNodeName().equals("#text"))  continue;

                //Condition
                if (ruleNodeChild.getNodeName().equals("Condition")) {
                    conditionCount++;
                    //check every child node of condition
                    for (Node conditionNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (conditionNodeChild.getNodeName().equals("#text"))  continue;

                        if (conditionCount == 1) {
                            processConditionAndActionChildNodes(conditionsOne, conditionNodeChild);
                        } else if (conditionCount == 2) {
                            processConditionAndActionChildNodes(conditionsTwo, conditionNodeChild);
                        }
                    }
                } // end of if equals Condition

                //Actions
                if (ruleNodeChild.getNodeName().equals("Actions")) {
                    //check every child node of actions
                    for (Node actionsNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (actionsNodeChild.getNodeName().equals("#text"))  continue;

                        //Message Action
                        if (actionsNodeChild.getNodeName().equals("Action")) {
                            Integer[] action = new Integer[4];

                            //check every child node of message action
                            for (Node actionNodeChild : iterable(actionsNodeChild.getChildNodes())) {
                                if (actionNodeChild.getNodeName().equals("#text")) continue;

                                processConditionAndActionChildNodes(action, actionNodeChild);
                            }
                            actions.add(action);
                        }

                        //Wait Action
                        if (actionsNodeChild.getNodeName().equals("Wait")) {
                            //add a IntegerArray containing only the wait time
                            Integer[] wait = new Integer[1];
                            wait[0] = Integer.parseInt(actionsNodeChild.getTextContent());
                            actions.add(wait);
                        }
                    }
                }

            }

            //  Iterating over one rule block done, add conditions and actions to new rule
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
            case "BitValue":
                targetArray[3] = Integer.parseInt(node.getTextContent());
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
    private Iterable<Node> iterable(final NodeList nodeList) {
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
