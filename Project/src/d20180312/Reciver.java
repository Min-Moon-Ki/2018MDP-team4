package d20180312;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Reciver {

	private static final int datarate = 9600;
	private static final int[] uuid = {0,0,0};

	private BufferedInputStream inpstr;
	private BufferedOutputStream outpstr;
	
	private SerialPort sp;
	private String commName = "/dev/ttyACM0";
	private CommPort cmp;
	
	private boolean flag = false;
	private StringBuilder sb;
	
	public Reciver() {
		sb = new StringBuilder();
		
		@SuppressWarnings("rawtypes")
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
							inpstr = new BufferedInputStream(sp.getInputStream());
							outpstr = new BufferedOutputStream(sp.getOutputStream());
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
	
	private void processing(int num) {
		switch(num) {
		case 's' :
			flag = true;
			break;
		case 'e' :
			flag = false;
			break;
		}
		if(flag) sb.append(num);
	}
	
	public boolean send(int send) {
		try {
			outpstr.write(send);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void run(){
		try {
			int temp;
			while(true)
			{
				temp = inpstr.read();
				if(temp != -1) {
					processing(temp);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(inpstr != null) inpstr.close();
				if(outpstr != null) outpstr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}