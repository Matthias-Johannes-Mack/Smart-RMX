package xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.security.sasl.SaslException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static XML_ActionType actionType;

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

    //Todo boolean if its a Byte rule
    private boolean byteRule = false;


    /**
     * reads in the rules from the file and saves them to the Factory Class.
     * A rule consists of
     * - Integer Array for each of the two Conditions [Bus, SystemAddress, Bit]
     * - List containing Integer Arrays for each Action [Bus, SystemAddress, Bit, Bitvalue] and Arrays for the Wait operation [time in ms]
     */
    private void readXML() throws SAXException {
        /**
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
            Integer[] conditionOne = new Integer[6];
            // contains the second condition
            Integer[] conditionTwo = new Integer[6];
            // List containing all the actions for one rule als an integer Array
            ArrayList<XML_ActionWrapper> actions = new ArrayList<>();


            // condition counter to know which condition Array to save to
            int conditionCount = 0;
            //iterate over every child node of the rule
            for (Node ruleNodeChild : iterable(nodeList)) {
                if (ruleNodeChild.getNodeName().equals("#text")) continue;


                //Condition
                if (ruleNodeChild.getNodeName().equals(XML_Constants.BitConditions)) {
                    //check every child node of BitConditions
                    for (Node bitConditionNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (bitConditionNodeChild.getNodeName().equals("#text")) continue;
                        //TODO anpassen für bitcondition
                        //Condition
                        if (bitConditionNodeChild.getNodeName().equals(XML_Constants.Condition)) {
                            conditionCount++;
                            //check every child node of condition
                            for (Node conditionNodeChild : iterable(bitConditionNodeChild.getChildNodes())) {
                                if (conditionNodeChild.getNodeName().equals("#text")) continue;

                                if (conditionCount == 1) {
                                    processConditionChildNodes(conditionOne, conditionNodeChild);
                                } else if (conditionCount == 2) {
                                    processConditionChildNodes(conditionTwo, conditionNodeChild);
                                }
                            }
                        } // end of if equals Condition

                    }
                } // end of if equals BitConditions

                //Condition
                if (ruleNodeChild.getNodeName().equals(XML_Constants.ByteConditions)) {
                    //check every child node of BitConditions
                    for (Node byteConditionNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (byteConditionNodeChild.getNodeName().equals("#text")) continue;
                        //TODO anpassen für bitcondition
                        //Condition
                        if (byteConditionNodeChild.getNodeName().equals(XML_Constants.Condition)) {
                            conditionCount++;
                            //check every child node of condition
                            for (Node conditionNodeChild : iterable(byteConditionNodeChild.getChildNodes())) {
                                if (conditionNodeChild.getNodeName().equals("#text")) continue;

                                if (conditionCount == 1) {
                                    processConditionChildNodes(conditionOne, conditionNodeChild);
                                } else if (conditionCount == 2) {
                                    processConditionChildNodes(conditionTwo, conditionNodeChild);
                                }
                            }
                        } // end of if equals Condition

                    }
                } // end of if equals BitConditions



                // the read XML tag is a "Actions"
                if (ruleNodeChild.getNodeName().equals(XML_Constants.Actions)) {
                    //check every child node of actions
                    for (Node actionsNodeChild : iterable(ruleNodeChild.getChildNodes())) {
                        if (actionsNodeChild.getNodeName().equals("#text")) continue;

                        //Message Action
                        if (actionsNodeChild.getNodeName().equals(XML_Constants.BitAction) || actionsNodeChild.getNodeName().equals(XML_Constants.ByteAction)) {
                            int[] actionArray = new int[4];

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

            System.err.println("byterule in XML_READ: " + byteRule);
            //  Iterating over one rule block done, add conditions and actions to new rule
            if (!byteRule) {
                // need to shorten Integer Array to length 4, since this is required for bit Rule
                //TODO conditionType Array also for the ArrayLength
                Integer[] conditionOneAdress = Arrays.copyOfRange(conditionOne, 0, 4);
                Integer[] conditionTwoAdress = Arrays.copyOfRange(conditionTwo, 0, 4);
                Factory.addBitRule(conditionOneAdress, conditionTwoAdress, actions);
            } else {
                // the rule is a byte rule

                //TODO refactor
                boolean conditionsOneValueSet = false;
                boolean conditionsTwoValueSet = false;

                for(int i = 2; i <=5; i++) {
                    if(conditionOne[i] != null) {
                        conditionsOneValueSet = true;
                    }
                    if(conditionTwo[i] != null) {
                        conditionsTwoValueSet = true;
                    }
                }

                if(!conditionsOneValueSet || !conditionsTwoValueSet) {
                    throw new SAXException("ByteCondition contains no value");
                }

                Factory.addByteRule(conditionOne, conditionTwo, actions);
            }


        }
    }

    /**
     * helper method for readXML
     * processes the Bus, SystemAdress and Bit node of the Action and Condition nodes and puts them in the given array
     *
     * @param targetArray array to which the node values should be written
     * @param node        node whose content should be written to the array
     */
    private void processConditionChildNodes(Integer[] targetArray, Node node) {
        switch (node.getNodeName()) {
            case XML_Constants.Bus:
                targetArray[0] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.SystemAddress:
                targetArray[1] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.Bit:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                byteRule = false;
                break;
            case XML_Constants.BitValue:
                targetArray[3] = Integer.parseInt(node.getTextContent());
                break;
            case XML_Constants.Equal:
                targetArray[2] = Integer.parseInt(node.getTextContent());
                byteRule = true;
                break;
            case XML_Constants.NotEqual:
                targetArray[3] = Integer.parseInt(node.getTextContent());
                byteRule = true;
                break;
            case XML_Constants.Bigger:
                targetArray[4] = Integer.parseInt(node.getTextContent());
                byteRule = true;
                break;
            case XML_Constants.Smaller:
                targetArray[5] = Integer.parseInt(node.getTextContent());
                byteRule = true;
                break;
        }

        System.err.println("bytrule in processConditionChildNodes: " + byteRule);
    }

    /**
     * helper method for readXML
     * processes the Bus, SystemAdress and Bit node of the Action and Condition nodes and puts them in the given array
     * <p>
     * for a bit action message targetArray: [Bus][Systemadress][Bit][Bitvalue]
     * for a byte action message targetArray: [Bus][Systemadress][ByteValue]
     *
     * @param node        node whose content should be written to the array
     */
    private int[] processActionChildNodes(int[]targetArray, Node node) {

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
                if(node.getTextContent().equals(XML_Constants.Toggle)) {
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

        return targetArray;
    }


    //TODO Delete
    public void test() {
        System.out.println("Test:");
        System.out.println("Number of Rules " + Factory.getBitRules().size());
        for (BitRule bitRule : Factory.getBitRules()) {
            System.out.println("----Rule----");
            System.out.println("Condition1: " + Arrays.toString(bitRule.getConditionOne()));
            System.out.println("Condition2:" + Arrays.toString(bitRule.getConditionOne()));
//            for (Integer[] action : bitRule.getActions()) {
//                System.out.println("Action: " + Arrays.toString(action));
//            }
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
