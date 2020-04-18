package matrix.byteMatrix;


import java.util.Arrays;
import java.util.HashMap;

/**
 * Wrapper Object for a Byte Rule Condition
 */
public class ByteCondition implements Comparable<ByteCondition> {

    /**
     * Holds the different Condition types of the byte condition if they were set in the xml
     */
    HashMap<ByteConditionType, Integer> conditionTypeValue;

    /**
     * Integer Array for Conditions address [Bus, SystemAddress]
     */
    private Integer[] conditionAdress;

    /**
     * constructor
     * @param conditionAdress Address of the condition [Bus, SystemAddress]
     */
    public ByteCondition(Integer[] conditionAdress) {
        conditionTypeValue = new HashMap<>();
        this.conditionAdress = conditionAdress;
    }

    /**
     *checks if all the set conditions in the ByteCondition are met for a given byte value
     *
     * @param currentByteValue byte value to chek against the set conditions of the ByteCondition
     * @return true if the all the set conditions are true for the given byte value, else otherwise
     */
    public boolean checkCondition(int currentByteValue) {
        //indicates whether all the set conditions of the ByteCondition are met with the given byte value
        boolean result = true;

        // only iterates through existing condtionTypes in the Map
        loop: for (ByteConditionType conditionType : conditionTypeValue.keySet()) {

            switch (conditionType) {
                case EQUAL:
                    if (!checkEqual(currentByteValue)) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case NOTEQUAL:
                    if (!checkNotEqual(currentByteValue)) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case BIGGER:
                    if (!checkBigger(currentByteValue)) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case SMALLER:
                    if (!checkSmaller(currentByteValue)) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
            }
        }

        return result;
    }

    private boolean checkEqual(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.EQUAL));
        return compareResult == 0;
    }

    private boolean checkNotEqual(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.NOTEQUAL));
        return compareResult != 0;
    }

    private boolean checkSmaller(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.SMALLER));
        return compareResult < 0;
    }

    private boolean checkBigger(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.BIGGER));
        return compareResult > 0;
    }

    /*
        GETTER
     */
    /**
     * getter for the conditions address
     * @return Conditions Address [Bus, SystemAddress]
     */
    public Integer[] getConditionAdress() {
        return conditionAdress;
    }

    /**
     * getter for conditionTypeValue HashMap of the condition
     * @return conditionTypeValue HashMap of the condition
     */
    public HashMap<ByteConditionType, Integer> getConditionTypeValue() {
        return conditionTypeValue;
    }


    /*
       SETTER
    */
    public void setEqual(int conditionEqualValue) {
        conditionTypeValue.put(ByteConditionType.EQUAL, conditionEqualValue);
    }

    public void setNotEqual(int conditionNotEqualValue) {
        conditionTypeValue.put(ByteConditionType.NOTEQUAL, conditionNotEqualValue);
    }

    public void setBigger(int conditionBiggerValue) {
        conditionTypeValue.put(ByteConditionType.BIGGER, conditionBiggerValue);
    }

    public void setSmaller(int conditionSmallerValue) {
        conditionTypeValue.put(ByteConditionType.SMALLER, conditionSmallerValue);
    }

    /**
     * o1.compareTo( o2 ) < 0 o1 < o2
     * o1.compareTo( o2 ) ==     o1 == o2
     * o1.compareTo( o2 ) > 0 o1 > o2
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(ByteCondition o) {

        if (this.conditionAdress[0] < o.conditionAdress[0]) {
            // this is smaller
            return -1;
        } else if (this.conditionAdress[0] > o.conditionAdress[0]) {
            // o is smaller
            return 1;
        } else {
            // both conditions are in the same bus
            if (this.conditionAdress[1] < o.conditionAdress[1]) {
                // this is smaller
                return -1;
            } else if (this.conditionAdress[1] > o.conditionAdress[1]) {
                // o is smaller
                return 1;
            }
        }

        // both conditions are in the same bus and systemadress
        return 0;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ByteCondition byteCondition = (ByteCondition) o;
        return conditionTypeValue.equals(byteCondition.conditionTypeValue) &&
                Arrays.equals(conditionAdress, byteCondition.conditionAdress);
    }


}
