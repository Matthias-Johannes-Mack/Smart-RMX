package test;


import Utilities.ByteUtil;

public class TestUtils {
	public static void main(String[] args) {
		byte TenAsByte = (byte) 10;
		byte threeAsByte = (byte) 3;
		byte oneHundredAndNintyFive = (byte) 195;


		//Testing bitIsSet()
		System.out.println(ByteUtil.bitIsSet(TenAsByte,0)); //false
		System.out.println(ByteUtil.bitIsSet(TenAsByte,1)); //true
		System.out.println(ByteUtil.bitIsSet(TenAsByte,2)); //false
		System.out.println(ByteUtil.bitIsSet(TenAsByte,3)); //true
		System.out.println(ByteUtil.bitIsSet(TenAsByte,4)); //false
		System.out.println(ByteUtil.bitIsSet(TenAsByte,5)); //false
		System.out.println(ByteUtil.bitIsSet(TenAsByte,6)); //false
		System.out.println(ByteUtil.bitIsSet(TenAsByte,7)); //false

		// Testing setBitAtPos()
		System.out.println(ByteUtil.setBitAtPos(TenAsByte,0, 1));  //11
		System.out.println(ByteUtil.setBitAtPos(TenAsByte,1, 0));  //8

		// Testing convertToInt()
		System.out.println(ByteUtil.convertToInt(threeAsByte, threeAsByte)); //771
		System.out.println(ByteUtil.convertToInt(oneHundredAndNintyFive, threeAsByte)); //963

		// Testing convertToBytes()
		byte[] solution = ByteUtil.convertToBytes(963);
		for(byte byteSolution : solution) {
			System.out.println(byteSolution);//3 & -61 = 195 (-61 in Decimal from signed 2's complement form)
		}
	}
}
