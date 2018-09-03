package testing;

import java.util.Scanner;

import d20180312.Bluetooth;
import d20180312.FPGA_Bluetooth;

public class blue {

	blue() {
		Bluetooth bt = new FPGA_Bluetooth();
		
		bt.connectBluetooth("4JO");
		
		Scanner kb = new Scanner(System.in);
		
		while(true) {
			switch(kb.nextLine()) {
				case "GO" :
					byte by = toByte("00010011");
					System.out.println(by);
					bt.writeData(by);
					break;
				case "OFF" :
					byte byt = toByte("00000001");
					System.out.println(byt);
					bt.writeData(byt);
					break;
			}
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
