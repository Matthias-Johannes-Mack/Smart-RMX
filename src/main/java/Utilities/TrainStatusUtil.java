package Utilities;

import java.util.*;

/**
 * Class containing utilities to figure out which buttons have been pressed if the Server sends message:
 *  <0x7c><0x08><0x28><ADRH><ADRL><F0F7><F8F15><F16F23>
 */
public class TrainStatusUtil {
    //Solution of the sum_up Method containing the Elements of the given set that add up to the given number
    private static Integer[] numberCombinationSolutions;
    /*
    length of the array that represents the Buttons adresse through the byte data <F0F7> or <F8F15>
    from the server message <0x7c><0x08><0x28><ADRH><ADRL><F0F7><F8F15><F16F23>
     */
    private static final int FBUTTONARRAYLENGTH = 8;

    /**
     * Sumsup recursivly, helper method for sum_up
     *
     * @param numbers elements containing the numbers that should add up to the target
     * @param target target that should be summed up to
     * @param partial partial solutions
     */
    private static void sumUpRecursive(ArrayList<Integer> numbers, int target, ArrayList<Integer> partial) {
        int s = 0;
        for (int x : partial) {
            s += x;
        }
        if (s == target) {
            Integer[] solution = new Integer[partial.size()];
            solution = partial.toArray(solution);
            numberCombinationSolutions = solution;
        }

        if (s >= target) {
            return;
        }

        for (int i = 0; i < numbers.size(); i++) {
            ArrayList<Integer> remaining = new ArrayList<>();
            int n = numbers.get(i);
            for (int j = i + 1; j < numbers.size(); j++) remaining.add(numbers.get(j));
            ArrayList<Integer> partial_rec = new ArrayList<>(partial);
            partial_rec.add(n);
            sumUpRecursive(remaining, target, partial_rec);
        }
    }

    /**
     * Combination Sum:
     * finds the elements in numbers that add up to the target numbers and returns the combination
     * uses helper method sumUpRecursive
     * Saves the Solution in numberCombinationSolutions
     *
     * @param numbers elements containing the numbers that should add up to the target
     * @param target target that should be summed up to, positive integer
     */
    private static void sum_up(ArrayList<Integer> numbers, int target) {
        sumUpRecursive(numbers, target, new ArrayList<>());
    }

    /**
     * Takes the the values from <F0F7> or <F8F15> or <F16F23> from the  <0x7c><0x08><0x28><ADRH><ADRL><F0F7><F8F15><F16F23>
     * messages and calculates which buttons are pressed in the given range of Buttons. Returns the which buttons are
     * pressed in an Array.
     * Values explained (F0 == Light)
     * The first Button of byte here F0 has the value 1:
     *  F0/F8/F16: 1
     *  F1/F9: 2
     *  F2/F10: 4
     *  F3/F11: 8
     *  F4/F12: 16
     *  F5/F13: 32
     *  F6/F14: 64
     *  F7/F15: -124
     *  The value of <F0F7> or <F8F15> or <F16F23> is the value of the buttons pressed
     *
     * @param value value of <F0F7> or <F8F15> or <F16F23>
     */
    private static Integer[] checkWhichButtonPressed(int value) {
        // Buttons F0 - F16: 0,1,2,4, ... 64, -128. -128 not included since algorithm doesnt work with negative numbers
        Integer[] elements = {1, 2, 4, 8, 16, 32, 64};

        //no button is pressed
        if (value == 0) {
            Integer[] solutionNoButtonPressed = new Integer[FBUTTONARRAYLENGTH];
            Arrays.fill(solutionNoButtonPressed, 0);
            return solutionNoButtonPressed;
        }

        // since sum_up can only calculate positive target numbers we need to change the calculation
        if (value < 0) {
            value += 128;
            sum_up(new ArrayList<>(Arrays.asList(elements)), value);

            Integer[] solution = numberCombinationSolutions;
            List<Integer> newList = new ArrayList<>(Arrays.asList(solution));
            newList.add(-128);
            numberCombinationSolutions = newList.toArray(solution);
        } else {
            sum_up(new ArrayList<>(Arrays.asList(elements)), value);
        }

        //transforms into array
        Integer[] buttonsPressed = new Integer[FBUTTONARRAYLENGTH];
        //initalize every thing with zero
        Arrays.fill(buttonsPressed, 0);
        for (Integer number : numberCombinationSolutions) {
            switch(number) {
                case 1:
                    buttonsPressed[0] = 1;
                    break;
                case 2:
                    buttonsPressed[1] = 1;
                    break;
                case 4:
                    buttonsPressed[2] = 1;
                    break;
                case 8:
                    buttonsPressed[3] = 1;
                    break;
                case 16:
                    buttonsPressed[4] = 1;
                    break;
                case 32:
                    buttonsPressed[5] = 1;
                    break;
                case 64:
                    buttonsPressed[6] = 1;
                    break;
                case -128:
                    buttonsPressed[7] = 1;
                    break;
                default:
                    break;
            }
        }
        return buttonsPressed;
    }

    /**
     *takes the button pressed message from the receiver and returns an array containing the id and the status of the buttons
     * [Train Number, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16]
     * The rest [Speed, Direction] to get the full array
     * [Train Number, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, Speed, Direction]
     * is not added yet TODO will not be added here probably
     * @param messageByte <0x28><ADRH><ADRL><F0F7><F8F15><F16F23>
     * @return [Train Number, F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16]
     */
    public static Integer[] getButtonsPressedArray(byte[] messageByte) {
        byte highByte = messageByte[1];
        byte lowByte =  messageByte[2];
        int ADRH = messageByte[1];
        int ADRL = messageByte[2];
        int F0F7 = messageByte[3];
        int F8F15 = messageByte[4];
        int F16F23 = messageByte[5];

        //get button values for each block
        Integer[] F0F7Array = checkWhichButtonPressed(F0F7);
        Integer[] F8F15Array = checkWhichButtonPressed(F8F15);

        // F16F23: RMX only uses F16 so the value should always be 0 or 1
        Integer[] F16F23Array = new Integer[1];
        if(F16F23 == 0) {
            F16F23Array[0] = 0;
        } else if(F16F23 == 1) {
            F16F23Array[0] = 1;
        } else {
            F16F23Array  = checkWhichButtonPressed(F16F23);
            System.out.println("<F16F23> Block should only contain 0 or 1");
        }

        int trainID;
        if(ADRH != 0) {
            trainID = ByteUtil.convertToInt(lowByte, highByte);
        } else {
            trainID = ADRL;
        }

        //combine to one array
        ArrayList<Integer> solutionList = new ArrayList<>();
        solutionList.add(trainID);
        Collections.addAll(solutionList, F0F7Array);
        Collections.addAll(solutionList, F8F15Array);
        Collections.addAll(solutionList, F16F23Array);

        return solutionList.toArray(new Integer[solutionList.size()]);
    }

    /**
     * Takes the movement message sendt from the server <0x7c><0x07><0x24><ADRH><ADRL><SPEED><DIR> and puts it in an
     * Integer Array: [Train Number, Speed, Direction]
     *
     * @param messageByte <0x24><ADRH><ADRL><SPEED><DIR>
     * @return [Train Number, Speed, Direction]
     */
    public static Integer[] getSpeedArray(byte[] messageByte) {
        byte highByte = messageByte[1];
        byte lowByte = messageByte[2];
        int ADRH = messageByte[1];
        int ADRL = messageByte[2];
        int speed = messageByte[3];
        int direction = messageByte[4];

        int trainID;
        if (ADRH != 0) {
            trainID = ByteUtil.convertToInt(lowByte, highByte);
        } else {
            trainID = ADRL;
        }

        return new Integer[]{trainID, speed, direction};
    }

    public static void main(String[] args) {

        Integer[] solution = checkWhichButtonPressed(-13);
        System.out.println(Arrays.toString(numberCombinationSolutions));
        System.out.println(Arrays.toString(solution));

        System.out.println("---");

        byte[] msg = new byte[] { 0x28, 0, 99, 12, 12, 0};
        Integer[] solution2 = getButtonsPressedArray(msg);
        System.out.println(Arrays.toString(solution2));
    }

}
