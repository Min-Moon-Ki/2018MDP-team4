package temp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Wifi {

	class writer extends Thread {
		private BufferedOutputStream out;
		private Scanner sc = new Scanner(System.in);
		writer(OutputStream out) {
			this.out = new BufferedOutputStream(out);
		}
		public void run() {
			try {
				while(true) {
					String msg = sc.nextLine();
					char[] ch = msg.toCharArray();
					for(int i =0; i <ch.length; i++)
						out.write(ch[i]);
					out.flush();
					System.out.println("send done");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class reader extends Thread {
		private InputStream in;
		reader(InputStream in) {
			this.in = in;
		}
		public void run() {
			try {
				while(true)	{
					int read = in.read();
					if(read != -1) {
						System.out.println((char)read);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private InputStream in;
	private OutputStream out;
	CommPort comm;
	Wifi() {
		try {
			CommPortIdentifier cofi = CommPortIdentifier.getPortIdentifier("COM7");
			System.out.println("COM7");
	        if ( !(cofi.isCurrentlyOwned()) ) {
	        	System.out.println("NOT OWNED");
				CommPort commPort = cofi.open(this.getClass().getName(),2000);
				if ( commPort instanceof SerialPort )
		        {
		            //포트 설정(통신속도 설정. 기본 9600으로 사용)
		            SerialPort serialPort = (SerialPort) commPort;
		            serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
		            
		            //Input,OutputStream 버퍼 생성 후 오픈
		            in = serialPort.getInputStream();
		            out = serialPort.getOutputStream();
		            
		            reader re = new reader(in);
		            writer wr = new writer(out);
		            re.start();
		            //wr.start();
		            System.out.println("done");
		        }
	        }
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
