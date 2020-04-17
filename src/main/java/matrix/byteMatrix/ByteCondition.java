package matrix.byteMatrix;


import java.util.Arrays;
import java.util.HashMap;

public class ByteCondition implements Comparable<ByteCondition> {

    HashMap<ByteConditionType, Integer> conditionTypeValue;

    /**
     * Integer Array for first Conditions [Bus, SystemAddress]
     */
    private Integer[] conditionAdress;

    public ByteCondition(Integer[] conditionAdress) {
        conditionTypeValue = new HashMap<>();
        this.conditionAdress = conditionAdress;
    }

     /*
        Check
     */
    public boolean checkCondition(int currentByteValue) {

        boolean result = true;

        // only iterates through existing condtionTypes in the Map
        loop: for (ByteConditionType conditionType : conditionTypeValue.keySet()) {

            System.out.println("KEYSET IN CHECK OF CONDITION: " + Arrays.toString(conditionTypeValue.keySet().toArray()));

            System.err.println("IN SWITCH " + conditionType.toString() + " " + currentByteValue);

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

        System.err.println("RESULT " + result);

        return result;

    }

    private boolean checkEqual(int currentByteValue) {


        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.EQUAL));


        if (compareResult == 0) {
            return true;
        }

        return false;

    }

    private boolean checkNotEqual(int currentByteValue) {

        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.NOTEQUAL));

        if (compareResult != 0) {
            return true;
        }

        return false;

    }

    private boolean checkSmaller(int currentByteValue) {

        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.SMALLER));

        if (compareResult < 0) {
            return true;
        }

        return false;

    }

    private boolean checkBigger(int currentByteValue) {

        int compareResult = Integer.valueOf(currentByteValue).compareTo(conditionTypeValue.get(ByteConditionType.BIGGER));

        if (compareResult > 0) {
            return true;
        }

        return false;

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

    public Integer[] getConditionAdress() {
        return conditionAdress;
    }

    public HashMap<ByteConditionType, Integer> getConditionTypeValue() {
        return conditionTypeValue;
    }
}
