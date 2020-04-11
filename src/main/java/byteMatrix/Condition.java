package byteMatrix;


import java.util.Arrays;
import java.util.HashMap;

public class Condition implements Comparable<Condition> {

    HashMap<ConditionType, Byte> conditionTypeValue;

    /**
     * Integer Array for first Conditions [Bus, SystemAddress]
     */
    private Integer[] conditionAdress;

    public Condition(Integer[] conditionAdress) {
        this.conditionAdress = conditionAdress;
    }

     /*
        Check
     */

    public boolean checkCondition(byte currentByteValue) {

        boolean result = true;

        // only iterates through existing condtionTypes in the Map
        loop: for (ConditionType conditionType : conditionTypeValue.keySet()) {

            switch (conditionType) {
                case EQUAL:
                    if (checkEqual(currentByteValue) == false) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case NOTEQUAL:
                    if (checkNotEqual(currentByteValue) == false) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case BIGGER:
                    if (checkBigger(currentByteValue) == false) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
                case SMALLER:
                    if (checkSmaller(currentByteValue) == false) {
                        result = false;
                        // if one condition already is false no need to check further -> break out of whole loop
                        break loop;
                    }
                    break;
            }
        }

        return result;

    }

    private boolean checkEqual(byte currentByteValue) {


        int compareResult = Byte.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ConditionType.EQUAL));

        if (compareResult == 0) {
            return true;
        }

        return false;

    }

    private boolean checkNotEqual(byte currentByteValue) {

        int compareResult = Byte.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ConditionType.NOTEQUAL));

        if (compareResult != 0) {
            return true;
        }

        return false;

    }

    private boolean checkSmaller(byte currentByteValue) {

        int compareResult = Byte.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ConditionType.SMALLER));

        if (compareResult < 0) {
            return true;
        }

        return false;

    }

    private boolean checkBigger(byte currentByteValue) {

        int compareResult = Byte.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ConditionType.BIGGER));

        if (compareResult > 0) {
            return true;
        }

        return false;

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
    public int compareTo(Condition o) {

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

    /*
       SETTER
    */
    public void setEqual(byte conditionEqualValue) {
        conditionTypeValue.put(ConditionType.BIGGER, conditionEqualValue);
    }

    public void setNotEqual(byte conditionNotEqualValue) {
        conditionTypeValue.put(ConditionType.BIGGER, conditionNotEqualValue);
    }

    public void setBigger(byte conditionBiggerValue) {
        conditionTypeValue.put(ConditionType.BIGGER, conditionBiggerValue);
    }

    public void setSmaller(byte conditionSmallerValue) {
        conditionTypeValue.put(ConditionType.BIGGER, conditionSmallerValue);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Condition condition = (Condition) o;
        return conditionTypeValue.equals(condition.conditionTypeValue) &&
                Arrays.equals(conditionAdress, condition.conditionAdress);
    }

    public Integer[] getConditionAdress() {
        return conditionAdress;
    }
}
