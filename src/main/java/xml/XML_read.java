package xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Class that reads a XML-File into a buffer
 *
 * @author Matthias Mack 3316380
 */
class XML_read {

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
     * Returns singleton XML_read instance
     *
     * @return XML_read Singleton instance
     */
    public static synchronized XML_read getXML_read() {
        if (instance == null) {
            instance = new XML_read();
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * indicates of which tye the current action is
     */
    private static XML_ActionType actionType;

    /**
     * the xsd schema cannot specify if the conditions of the byte conditions are correct
     * it could happen that none of Equal, NotEqual, Bigger, Smaller is selected which cant be checked in
     * Boolean
     * Array containing information if one of the above is set for a byteCondition
     * [ConditionOneByteValueSet, ConditionTwoByteValueSet]
     */
    private boolean[] byteConditionValueSet = new boolean[2];

    /**
     * xml document that is read in
     */
    private org.w3c.dom.Document xmlDoc;

    /**
     * instance of Factory
     */
    private Factory factory;

    // END OF ATTRIBUTES -------------------------------------------------------

    /**
     * constructor
     * reads and processes the given xml document
     *
     * @param xmlDoc xml Document
     */
    public void processXMLDocument(org.w3c.dom.Document xmlDoc) throws SAXException {
        if (xmlDoc != null) {
            this.xmlDoc = xmlDoc;
        }
        factory = Factory.getFactory();
        readXML();
    }

    /**
     * reads in the rules from the file and saves them to the Factory Classs
     *
     * @throws SAXException throws exception if byte Rule does not contain any of these in XML Document: Equal, NotEqual, Bigger, Smaller
     */
    private void readXML() throws SAXException {
        /*
         contains NodeLists containing all of the child elements of a Rule Node. Each index is for a different Rule node
         */
        ArrayList<NodeList> ruleNodeChildrenArrList = new ArrayList<>();

        if (xmlDoc == null) {
            throw new IllegalArgumentException("XML Document cannot be NULL");
        }

        xmlDoc.getDocumentElement().normalize();

        //gets the rule child nodes
        for (Node node : iterable(xmlDoc.getElementsByTagName(XML_Constants.Rule))) {
            NodeList nodeList = node.getChildNodes();
            ruleNodeChildrenArrList.add(nodeList);
        }

        //iterate over every rule nodes children
        for (NodeList nodeList : ruleNodeChildrenArrList) {
            // contains the first condition
            Integer[] conditionOne = null;
            // contains the second condition
            Integer[] conditionTwo = null;
            // List containing all the actions for one rule als an integer Array
            ArrayList<XML_ActionWrapper> actions = new ArrayList<>();
            // set to default
            byteConditionValueSet[0] = false;
            byteConditionValueSet[1] = false;
            // indicates of which type the current read in conditions are
            XML_ConditionTypes conditionType = null;


            //iterate over every child node of the rule
            for (Node ruleNodeChild : iterable(nodeList)) {
                if (ruleNodeChild.getNodeName().equals("#text")) continue;

                /*
                Conditions
                 */
                if (ruleNodeChild.getNodeName().equals(XML_Constants.BitConditions)) {
                    conditionType = XML_ConditionTypes.BIT_CONDITION;

                    //[Bus, Systemadddess,Bit, Bitvalue]
                    conditionOne = new Integer[XML_ConditionTypes.BIT_CONDITION.ARRAY_LENGTH];
                    conditionTwo = new Integer[XML_ConditionTypes.BIT_CONDITION.ARRAY_LENGTH];

                    //check every child node of BitConditions
                    iterateOverConditionChildNodes(conditionOne, conditionTwo, ruleNodeChild);
                }

                if (ruleNodeChild.getNodeName().equals(XML_Constants.ByteConditions)) {
                    conditionType = XML_ConditionTypes.BYTE_CONDITION;


                    //[Bus, Systemaddress, Equals, NotEquals, Bigger, Smaller]
                    conditionOne = new Integer[XML_ConditionTypes.BYTE_CONDITION.ARRAY_LENGTH];
                    conditionTwo = new Integer[XML_ConditionTypes.BYTE_CONDITION.ARRAY_LENGTH];

                    //check every child node of Bytecondition
                    iterateOverConditionChildNodes(conditionOne, conditionTwo, ruleNodeChild);
                }

                /*
                Actions
                 */
                // the read XML tag is a "Actions"
                if (ruleNodeChild.getNodeName().equals(XML_Constants.Actions)) {
                    //check every child node of actions
                    for (Node actionsNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (actionsNodeChild.getNodeName().equals("#text")) continue;

                        //Message Action
                        if (actionsNodeChild.getNodeName().equals(XML_Constants.BitAction) || actionsNodeChild.getNodeName().equals(XML_Constants.ByteAction)) {
                            // Array of the max length of the action arrays that holds all the information regardless of action type which will be processed later
                            int[] actionArray = new int[XML_ActionType.MAX_LENGTH_OF_ACTION_ARRAY];

                            //check every child node of message action
                            for (Node actionNodeChild : iterable(actionsNodeChild.getChildNodes())) {
                                if (actionNodeChild.getNodeName().equals("#text")) continue;

                                // fill the actionArray with the given Data from the XML
                                processActionChildNodes(actionArray, actionNodeChild);
                            }

                            // add XML_ActionWrapper to List of actions for this rule
                            actions.add(new XML_ActionWrapper(actionArray, actionType));
                        }

                        // the read XML tag is a Wait
                        if (actionsNodeChild.getNodeName().equals(XML_Constants.Wait)) {
                            //add a IntegerArray containing only the wait time
                            int[] wait = new int[1];
                            wait[0] = Integer.parseInt(actionsNodeChild.getTextContent());
                            actions.add(new XML_ActionWrapper(wait, XML_ActionType.WAIT));
                        }
                    }
                }

            }

            //  Iterating over one rule block done, add conditions and actions to new rule
            if (conditionType == XML_ConditionTypes.BIT_CONDITION) {
                factory.addBitRule(conditionOne, conditionTwo, actions);
            }
            if (conditionType == XML_ConditionTypes.BYTE_CONDITION) {
                /*
                 the xsd schema cannot specify if the conditions of the byte conditions are correct
                 it could happen that none of Equal, NotEqual, Bigger, Smaller is selected which cant be checked in
                 the xsd and must be checked here
                 throws an Exception if none of the values is set in the xml file
                 */
                if (!byteConditionValueSet[0] || !byteConditionValueSet[1]) {
                    throw new SAXException("ByteCondition contains no value");
                }

                factory.addByteRule(conditionOne, conditionTwo, actions);
            }

        }
    }

    /**
     * helper method for readXML
     * iterates over the child nodes of a bit or byte condition and processes them using processConditionChildNodes
     *
     * @param conditionOne           Integer Array for saving first condition
     * @param conditionTwo           Integer Array for saving second condition
     * @param bitOrByteConditionNode child node of the rule (byte or bit condition node) whoms children should be processed
     */
    private void iterateOverConditionChildNodes(Integer[] conditionOne, Integer[] conditionTwo, Node bitOrByteConditionNode) {
        // condition counter to know which condition Array to save to
        int conditionCount = 0;

        //iterate over every child node
        for (Node bitOrByteConditionNodeChild : iterable(bitOrByteConditionNode.getChildNodes())) {
            if (bitOrByteConditionNodeChild.getNodeName().equals("#text")) continue;
            //Condition
            if (bitOrByteConditionNodeChild.getNodeName().equals(XML_Constants.Condition)) {
                conditionCount++;
                //check every child node of condition
                for (Node conditionNodeChild : iterable(bitOrByteConditionNodeChild.getChildNodes())) {
                    if (conditionNodeChild.getNodeName().equals("#text")) continue;

                    if (conditionCount == 1) {
                        processConditionChildNodes(conditionOne, conditionNodeChild, 0);
                    } else if (conditionCount == 2) {
                        processConditionChildNodes(conditionTwo, conditionNodeChild, 1);
                    }
                }
            }
        }
    }

    /**
     * helper method for iterateOverConditionChildNodes
     * processes the Bus, SystemAddress and Bit node of the Action and Condition nodes and puts them in the given array
     *
     * @param targetArray    array to which the node values should be written
     *                       for a bit condition: [Bus][Systemaddress][Bit][BitValue]
     *                       for a byte condition [Bus, Systemadress, Equals, NotEquals, Bigger, Smaller]
     * @param node           node whose content should be written to the array
     * @param conditionIndex index of the condition in byteConditionValueSet
     */
    private void processConditionChildNodes(Integer[] targetArray, Node node, int conditionIndex) {

        switch (node.getNodeName()) {
            case XML_Constants.Bus:
                targetArray[0] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.SystemAddress:
                targetArray[1] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.Bit:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.BitValue:
                targetArray[3] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.Equal:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                // indicates that the byteCondition is containing a correct definition
                byteConditionValueSet[conditionIndex] = true;
                break;
            case XML_Constants.NotEqual:
                targetArray[3] = Integer.parseInt(node.getTextContent());
                byteConditionValueSet[conditionIndex] = true;
                break;
            case XML_Constants.Bigger:
                targetArray[4] = Integer.parseInt(node.getTextContent());
                byteConditionValueSet[conditionIndex] = true;
                break;
            case XML_Constants.Smaller:
                targetArray[5] = Integer.parseInt(node.getTextContent());
                byteConditionValueSet[conditionIndex] = true;
                break;
        }
    }

    /**
     * helper method for readXML
     * processes the child nodes of a action node and saves them in thr target array, array length of target array is the max length of the different array types
     * and will be shortened accordingly int the XML_Action Wrapper
     * <p>
     * int Array for ActionMessageBit: [Bus][SystemAdrress][Bit][BitValue]
     * int Array for ActionMessageByte: [Bus][SystemAdrress][ByteValue]
     * int Array for waitAction: [Wait time in ms]
     * int Array for Byte Increment [bus][SystemAdrress][value]
     * int Array for Byte Decrement [bus][SystemAddress][ - value]
     * int Array for Bit Toggle [Bus][SystemAddress][Bit]
     *
     * @param targetArray target array to whom should be written
     * @param node        node whose content should be written to the array
     */
    private void processActionChildNodes(int[] targetArray, Node node) {

        switch (node.getNodeName()) {
            case XML_Constants.Bus:
                targetArray[0] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.SystemAddress:
                targetArray[1] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.Bit:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.BitValue:
                if (node.getTextContent().equals(XML_Constants.Toggle)) {
                    actionType = XML_ActionType.BITTOGGLE;
                } else {
                    targetArray[3] = Integer.parseInt(node.getTextContent());
                    actionType = XML_ActionType.BITMESSAGE;
                }
                break;
            case XML_Constants.ByteValue:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                actionType = XML_ActionType.BYTEMESSAGE;
                break;
            case XML_Constants.Increment:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                actionType = XML_ActionType.INCREMENT;
                break;
            case XML_Constants.Decrement:
                // decrement holds negative values
                targetArray[2] = Integer.parseInt(node.getTextContent()) * -1;
                actionType = XML_ActionType.DECREMENT;
                break;
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
