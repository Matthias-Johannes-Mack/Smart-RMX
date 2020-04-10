package Utilities;

import java.util.Arrays;

public class TestByteUtils {
    public static void main(String[] args) {

        byte byte1 = (byte) 254;

        System.out.println(Arrays.toString(ByteUtil.getByteArrayByByte(byte1)));


    }
}
