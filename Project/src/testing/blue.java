package testing;

import java.util.Scanner;

import d20180312.Bluetooth;
import d20180312.FPGA_Bluetooth;

public class blue {

	String str;
	
	blue() {
		Bluetooth bt = new FPGA_Bluetooth();
		
		bt.connectBluetooth(null);
		
		Scanner kb = new Scanner(System.in);
		while(true) {
			str = kb.nextLine();
			bt.writeData(toByte(str));
			System.out.println("send" + toByte(str));
		}
	}
	
	public static void main(String[] args) {
		new blue();
	}
	
	private byte toByte(String msg) {	
		byte b = 0;
		char[] ch = msg.toCharArray();
		for(int i = 0; i < ch.length; i++) {
			if(i > 7) break;
			else b += (ch[i]-'0') * Math.pow(2, - (i - 7));
		}
		return b;
	}
}
