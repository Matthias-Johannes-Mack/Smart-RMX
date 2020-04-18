package xml.xmlutilities;

import matrix.byteMatrix.ByteCondition;
import xml.XML_ActionWrapper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for printing the read in rules from the xml document, methods mainly used by factory
 */
public class RulePrintUtil {

    /**
     * prints out a byte rule
     * @param byteConditionOneObj conditionOne Object
     * @param byteConditionTwoObj conditionT&wo Object
     * @param actions ArrayList<XML_ActionWrapper>
     */
    public static void printByteRule(ByteCondition byteConditionOneObj, ByteCondition byteConditionTwoObj, ArrayList<XML_ActionWrapper> actions) {
        System.out.println("----RULE----");
        System.out.println("--Byte Condition--");

        System.out.println("-Condition 1-");
        System.out.println("Address: " + Arrays.toString(byteConditionOneObj.getConditionAdress()));
        byteConditionOneObj.getConditionTypeValue().forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("-Condition 2-");
        System.out.println("Address: " + Arrays.toString(byteConditionTwoObj.getConditionAdress()));
        byteConditionTwoObj.getConditionTypeValue().forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("--End Byte Condition--");

        printAction(actions);
    }

    /**
     * prints out a bit rule
     * @param conditionsOne [Bus, SystemAddress, Bit]
     * @param conditionsTwo [Bus, SystemAddress, Bit]
     * @param actions ArrayList<XML_ActionWrapper>
     */
    public static void printBitRule(Integer[] conditionsOne, Integer[] conditionsTwo, ArrayList<XML_ActionWrapper> actions) {
        System.out.println("----RULE----");
        System.out.println("--Bit Condition--");

        System.out.println("-Condition 1-");
        System.out.println(Arrays.toString(conditionsOne));

        System.out.println("-Condition 2-");
        System.out.println(Arrays.toString(conditionsTwo));

        System.out.println("--End Bit Condition--");

        printAction(actions);
    }

    /**
     * helper methods that prints out the actions
     * @param actions ArrayList<XML_ActionWrapper>
     */
    private static void printAction(ArrayList<XML_ActionWrapper> actions) {
        System.out.println("--Actions--");
        for(XML_ActionWrapper action : actions) {
            System.out.println(action.getType()+": "+Arrays.toString(action.getActionArray()));
        }
        System.out.println("--End Actions--");
        System.out.println("----END RULE----");
        System.out.println();
    }
}
