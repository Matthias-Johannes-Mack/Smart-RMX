package matrix.factory;


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

     /*-----------------------------------------------------------------------------------------------
      METHODS FOR CHECKING
     ----------------------------------------------------------------------------------------------*/

    /**
     * checks if all the set conditions in the ByteCondition are true with the given byteValue
     *
     * @param currentByteValue byteValue to check
     * @return true if the all the set conditions in the ByteCondition are true with the given byteValue, false otherwise
     */
    public boolean checkCondition(int currentByteValue) {

        // indicates if all the set conditions of the ByteCondition are true with the given byte value
        // initially true, is set to false if one condition is not true
        boolean result = true;

        // only iterates through existing condtionTypes in the Map
        loop: for (ByteConditionType conditionType : conditionTypeValue.keySet()) {

            switch (conditionType) {
                case EQUAL:
                    if (!checkEqual(currentByteValue)) {
                        result = false;
                        // if condition is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case NOTEQUAL:
                    if (!checkNotEqual(currentByteValue)) {
                        result = false;
                        // if condition is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case BIGGER:
                    if (!checkBigger(currentByteValue)) {
                        result = false;
                        // if condition is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case SMALLER:
                    if (!checkSmaller(currentByteValue)) {
                        result = false;
                        // if condition is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
            }
        }

        return result;
    }

    /**
     * checks the condition Equal by comparing the given byteValue with the byteValue of the Condition
     *
     * @param currentByteValue the currentByteValue of the systemadress
     * @return true if the currentByteValue is Equal to the byteValue of the condition, false otherwise
     */
    private boolean checkEqual(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.EQUAL));
        return compareResult == 0;
    }

    /**
     * checks the condition NotEqual by comparing the given byteValue with the byteValue of the Condition
     *
     * @param currentByteValue the currentByteValue of the systemadress
     * @return true if the currentByteValue is NotEqual to the byteValue of the condition, false otherwise
     */
    private boolean checkNotEqual(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.NOTEQUAL));
        return compareResult != 0;
    }

    /**
     * checks the condition Smaller by comparing the given byteValue with the byteValue of the Condition
     *
     * @param currentByteValue the currentByteValue of the systemadress
     * @return true if the currentByteValue is Smaller to the byteValue of the condition, false otherwise
     */
    private boolean checkSmaller(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.SMALLER));
        return compareResult < 0;
    }

    /**
     * checks the condition Bigger by comparing the given byteValue with the byteValue of the Condition
     *
     * @param currentByteValue the currentByteValue of the systemadress
     * @return true if the currentByteValue is Bigger to the byteValue of the condition, false otherwise
     */
    private boolean checkBigger(int currentByteValue) {
        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.BIGGER));
        return compareResult > 0;
    }

   /*-----------------------------------------------------------------------------------------------
      GETTER
     ----------------------------------------------------------------------------------------------*/

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


    /*-----------------------------------------------------------------------------------------------
      Setter
     ----------------------------------------------------------------------------------------------*/

    /**
     * sets the byteValue of the CondtionType EQUAL to the given byteValue
     * @param conditionEqualValue the byteValue to set the ConditionType EQUAL byteValue to
     */
    public void setEqual(int conditionEqualValue) {
        conditionTypeValue.put(ByteConditionType.EQUAL, conditionEqualValue);
    }

    /**
     * sets the byteValue of the CondtionType NOTEQUAL to the given byteValue
     * @param conditionNotEqualValue the byteValue to set the ConditionType NOTEQUAL byteValue to
     */
    public void setNotEqual(int conditionNotEqualValue) {
        conditionTypeValue.put(ByteConditionType.NOTEQUAL, conditionNotEqualValue);
    }

    /**
     * sets the byteValue of the CondtionType SMALLER to the given byteValue
     * @param conditionSmallerValue the byteValue to set the ConditionType SMALLER byteValue to
     */
    public void setSmaller(int conditionSmallerValue) {
        conditionTypeValue.put(ByteConditionType.SMALLER, conditionSmallerValue);
    }

    /**
     * sets the byteValue of the CondtionType BIGGER to the given byteValue
     * @param conditionBiggerValue the byteValue to set the ConditionType BIGGER byteValue to
     */
    public void setBigger(int conditionBiggerValue) {
        conditionTypeValue.put(ByteConditionType.BIGGER, conditionBiggerValue);
    }

     /*-----------------------------------------------------------------------------------------------
      CompareTo() and equals()
     ----------------------------------------------------------------------------------------------*/

    /**
     * Defines a order of ByteCondtions by comparing the conditionAdress.
     * A byteConditon is smaller if it has the smaller adress defined by the busId and systemadress.
     *
     * o1.compareTo( o2 ) < 0 o1 < o2
     * o1.compareTo( o2 ) ==     o1 == o2
     * o1.compareTo( o2 ) > 0 o1 > o2
     *
     * @param o the ByteCondition to compare to
     * @return 1 if the ByteConditon is bigger than the given ByteCondition,
     *         0 if the ByteConditon is equal to the given ByteCondition,
     *         -1 if the ByteConditon is smaller than the given ByteCondition
     */
    @Override
    public int compareTo(ByteCondition o) {

        if (this.conditionAdress[0] < o.conditionAdress[0]) {
            // this is smaller, since it is in the smaller bus
            return -1;
        } else if (this.conditionAdress[0] > o.conditionAdress[0]) {
            // o is smaller, since it is in the smaller bus
            return 1;
        } else {
            // both conditions are in the same bus
            if (this.conditionAdress[1] < o.conditionAdress[1]) {
                // this is smaller, since it has the smaller systemadress
                return -1;
            } else if (this.conditionAdress[1] > o.conditionAdress[1]) {
                // o is smaller, since it has the smaller systemadress
                return 1;
            }
        }

        // both conditions are in the same bus and systemadress
        return 0;
    }

    /**
     * ByteConditions are equal if their conditionTypeValue and conditionAdress is equal.
     * Used to avoid duplicates
     *
     * @param o a object to compare the current object with
     * @return true if the ByteConditions have equal conditionTypeValues and conditionAdresses
     */
    @Override
    public boolean equals(Object o) {

        // if same object
        if (this == o) {
            return true;
        }

        // if null or not even same class
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        // returns true if their conditionTypeValue and conditionAdress is equal, false otherwise
        ByteCondition byteCondition = (ByteCondition) o;
        return conditionTypeValue.equals(byteCondition.conditionTypeValue) &&
                Arrays.equals(conditionAdress, byteCondition.conditionAdress);
    }


}
