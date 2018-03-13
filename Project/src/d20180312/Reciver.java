package d20180312;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Reciver {

	private static final int datarate = 9600;
	private static final int[] uuid = {0,0,0};

	private InputStream inpstr;
	private OutputStream outpstr;
	
	private SerialPort sp;
	private String commName = "/dev/ttyACM0";
	private CommPort cmp;
	
	public Reciver() {
		Enumeration e = CommPortIdentifier.getPortIdentifiers();
				
		while(e.hasMoreElements()) {
			CommPortIdentifier comPort = (CommPortIdentifier) e.nextElement();
			if(comPort.getName().equals(commName)) {
				if(!comPort.isCurrentlyOwned()) {
					try {
						cmp = comPort.open(this.getClass().getName(),2000);
						if(cmp instanceof SerialPort) {
							sp = (SerialPort) cmp;
							sp.setSerialPortParams(datarate, 
									SerialPort.DATABITS_8, 
									SerialPort.STOPBITS_1,
									SerialPort.PARITY_NONE);
							inpstr = sp.getInputStream();
							outpstr = sp.getOutputStream();
						}
					} catch (PortInUseException e1) {
						e1.printStackTrace();
					} catch (UnsupportedCommOperationException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}	//if notOwned
				break;
			}	//if name == commName
		}	//while hasMoreElements
	}	//constructor
	
	public void run() {
		
	}
}
