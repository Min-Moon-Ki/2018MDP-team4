package d20180312;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class FPGA_Bluetooth implements Bluetooth, Runnable {

	private Process process; 
	private BufferedReader br;
	private BufferedWriter bw;
	
	@Override
	public void connectBluetooth(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append("bluetoothch");
	}
	
	@Override
	public byte readData() {
		
		return 0;
	}

	@Override
	public void writeData(byte data) {
		
	}
	
	@Override
	public void run() {
		
	}
	
	private Process createNewProcess(String cmd) {
        try {
			process = new ProcessBuilder(cmd).start();
	        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			return process;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	private Process createNewProcess(List<String> cmd) {
        try {
			process = new ProcessBuilder(cmd).start();
	        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			return process;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
}
