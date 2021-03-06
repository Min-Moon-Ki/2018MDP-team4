package d20180312;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;

public class Server {
	
	private final String BLUETOOTH_NAME = null;
	
	private Webcam webcam;
	private ServerSocket ssc;
	private CerrentClientInfo cinfo;
	private Bluetooth bluetooth;	//bluetooth communicate to FPGA
	private boolean reconnect = false;
	private Thread stateChecker,androidSender,bluetoothSender;
	private Server sv;
	private LinkedList<Byte> msglist;//commands to send FPGA
	
	/*
	static {
		Webcam.setDriver(new FsWebcamDriver());
	}
	
	
	/**
	 * create ServerSocket(port is 4240)
	 */
	Server() {
		try {
			ssc = new ServerSocket(4240);
			sv = this;
			msglist = new LinkedList<Byte>();
			bluetooth = new FPGA_Bluetooth();
			bluetoothSender = new Thread() {	//with FPGA
				@Override
				public void run() {
					bluetooth.connectBluetooth(BLUETOOTH_NAME); //bluetooth name
					while(true) {
						if(msglist.isEmpty()) {
							synchronized(bluetoothSender) {
								try {
									bluetoothSender.wait();
								} catch(InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							bluetooth.writeData(msglist.pop());
						}
					}
				}
			};
			bluetoothSender.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * accept only one socket
	 * @throws IOException
	 */
	private synchronized void acceptNewSocket() throws IOException {
		if(cinfo != null) cinfo.dispose();
		cinfo = new CerrentClientInfo(ssc);
		System.out.println("new Socket");
	}

	/**
	 * select Webcam number (select)
	 * default is 0
	 * @param select
	 */
	private void selectWebcam() {
		selectWebcam(0);
	}
	/**
	 * select Webcam number (select)
	 * default is 0
	 * @param select
	 */
	private void selectWebcam(int select) {
		System.out.println("start to get Webcam");
		List<Webcam> wclist = Webcam.getWebcams();
		if(wclist.isEmpty()) webcam = Webcam.getDefault();
		else webcam = wclist.get(select);
		if(webcam != null) {
			if(webcam.isOpen()) webcam.close();
			webcam.setViewSize(WebcamResolution.VGA.getSize());
			webcam.open();
			System.out.println("cam");
		} else {
			System.out.println("you don't have a cam");
		}
	}
	
	/**
	 * read String from Android App and setting message
	 * @throws IOException
	 */
	private void read() throws IOException {
		String msg = cinfo.in();	//read form socket
		if(msg != null) {
			System.out.println(msg);
			if(msg.equals("stop")) {	//server reset command 
				reconnect = true;
			}
			if(!msg.contains(":")) return; //Seperater ":"
			
	
			String[] ms;
			int[] integer;
			ms = msg.split(":");
			integer = new int[ms.length-1];
			for(int i = 1; i < ms.length; i++) {	//convert String to binary
				char[] ch = ms[i].toCharArray();
				for(int j = 0; j < ch.length; j++) {
					integer[i-1] += (ch[ch.length-1-j]-'0')*(int)Math.pow(10, j);
				}
			}
			
			switch(ms[0]) {
			case "ST" :
				//0000_0001;
				msglist.add(toByte("00000001"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "MF" :
				//0001_(speed)1
				msglist.add(toByte("0001"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "MB" : 
				//0010_(speed)1
				msglist.add(toByte("0010"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "TL" : 
				//0100_1001
				msglist.add(toByte("01001001"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "TR" : 
				//1000_1001
				msglist.add(toByte("10001001"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "FL" : 
				//0101_(speed)1
				msglist.add(toByte("0101"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "FR" :
				//1001_(speed)1 
				msglist.add(toByte("1001"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "BL" : 
				//0110_(speed)1
				msglist.add(toByte("0110"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "BR" : 
				//1010_(speed)1
				msglist.add(toByte("1010"+convertSpeed(ms[1])+"1"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
				
			case "AN" :
				//(angle)_0100
				msglist.add(toByte(convertAngle(ms[1])+"0100"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "CS" :
				msglist.add(toByte("01010000"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "CL" :
				msglist.add(toByte("00110000"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			case "CR" :
				msglist.add(toByte("00010000"));
				synchronized (bluetoothSender) {
					bluetoothSender.notify();
				}
				break;
			}
		}
		//msglist.add(tobyte("00001000"));	//�溸
	}
	
	/**
	 * start Thread to communicate with Android and FPGA
	 */
	public void start() {
		if(stateChecker == null)
			try {
				selectWebcam();
				acceptNewSocket();
				acceptNewSocket();	//android activity calls twice but idk;
				stateChecker = new Thread() {
					@Override
					public void run() {
						while(true) {
							if(androidSender.getState() == State.TERMINATED || reconnect == true) {
								reconnect = false;
								androidSender = null;
								sv.start();
								break;
							}
						}
					}
				};
				stateChecker.start();
				
				run();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		else {
			if(cinfo != null) { 
				cinfo.dispose();
				cinfo = null;
			}
			if(stateChecker != null) stateChecker = null;
			if(androidSender != null) androidSender = null;
			start();
		}
	}
	
	private final double frame = 60d;
	
	private long last = -1;
	private long delay = (long)(1000/frame);
	
	/**
	 * create android communicate Thread and bluetooth communicate Thread
	 */
	private synchronized void run () {
		androidSender = new Thread() {	//with Android
			@Override
			public void run() {
				
				while(cinfo != null) {
					if(webcam == null) {
						selectWebcam(1);
					}
					//streaming webcam to socket;
					long now = System.currentTimeMillis();
					try {
						if (now > last + delay) {
							if(webcam != null) { 
								BufferedImage img = webcam.getImage();
								if(img != null) cinfo.Imageout(img);
							}
							last = now;
						}
					} catch(IOException e) {
						if(e.getClass().equals(SocketException.class)) {
							cinfo.dispose();
							cinfo = null;
							System.out.println("Socket Closed");
						}
						
					}
					
					//receive command from Android for socket;
					try {
						read();
					} catch(NullPointerException e) {
						e.printStackTrace();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
				
			} 
		};
		
		androidSender.start();
	}
	/**
	 * convert integer to binary String
	 * @param angle
	 * @return
	 */
	private String convertAngle(String angle) {	 
		StringBuilder sb = new StringBuilder();
		int ang = 0;
		char[] ch = angle.toCharArray();
		for(int i = 0; i < ch.length; i++) ang += (ch[i]-'0') * Math.pow(10, i);

		/*
		sb.append(ang%2);
		sb.append(ang/2%2);
		sb.append(ang/4%2);
		sb.append(ang/8%2);
		*/
		
		for(int i = 0; i < 4; i++) {
			sb.append((ang / (int)Math.pow(2,i))%2);
		}
		
		return sb.reverse().toString();
	}
	/**
	 * convert integer to binary String code
	 * @param speed
	 * @return
	 */
	private String convertSpeed(String speed) {	
		String result = null;
		
		switch(speed) {
		case "0" : result = "000"; break;
		case "34" : result = "001"; break;
		case "68" : result = "010"; break;
		}
		
		return result;
	}
	/**
	 * convert String to byte
	 * @param msg
	 * @return
	 */
	private byte toByte(String msg) {	
		byte b = 0;
		char[] ch = msg.toCharArray();
		for(int i = 0; i < ch.length; i++) {
			if(i > 7) break;
			else b += (ch[i]-'0') * Math.pow(2, - (i - 7));
		}
		return b;
	}
	
	public static void main(String[] args) {
		Server sv = new Server();
		sv.start();
	}
	
}
