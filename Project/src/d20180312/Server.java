package d20180312;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class Server {
	
	private Webcam webcam;
	private ServerSocket ssc;
	private CerrentClientInfo cinfo;
	private Bluetooth bluetooth = null;
	private byte msg1 = 0;
	private Thread stateChecker,androidSender;
	private Server sv;
	
	/**
	 * create ServerSocket(default port is 4240)
	 */
	Server() {
		this(4240);
	}
	Server(int port) {
		try {
			ssc = new ServerSocket(port);
			sv = this;
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
	
	private void selectWebcam() {
		selectWebcam(0);
	}
	private void selectWebcam(int select) {
		List<Webcam> wclist = Webcam.getWebcams();
		if(wclist.isEmpty()) webcam = Webcam.getDefault();
		else webcam = wclist.get(select);
		if(webcam.isOpen()) webcam.close();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();
		System.out.println("cam");
	}
	
	/**
	 * read String from Android App 
	 * @throws IOException
	 */
	private void read() throws IOException {
		String msg = cinfo.in();
		if(msg != null) {
			if(!msg.contains(":")) return;
			
	
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
				msg1 = toByte("00000001");
				break;
			case "MF" :
				//0001_(speed)01
				msg1 = toByte("0001"+convertSpeed(ms[1])+"1");
				break;
			case "MB" : 
				//0010_(speed)01
				msg1 = toByte("0010"+convertSpeed(ms[1])+"1");
				break;
			case "TL" : 
				//0100_(speed)01
				msg1 = toByte("0100"+convertSpeed(ms[1])+"1");
				break;
			case "TR" : 
				//1000_(speed)01
				msg1 = toByte("1000"+convertSpeed(ms[1])+"1");
				break;
			case "FL" : 
				//0101_(speed)01
				msg1 = toByte("0101"+convertSpeed(ms[1])+"1");
				break;
			case "FR" :
				//1001_(speed)01 
				msg1 = toByte("1001"+convertSpeed(ms[1])+"1");
				break;
			case "BL" : 
				//0110_(speed)01
				msg1 = toByte("0110"+convertSpeed(ms[1])+"1");
				break;
			case "BR" : 
				//1010_(speed)01
				msg1 = toByte("1010"+convertSpeed(ms[1])+"1");
				break;
				
			case "LO" : 
				//0000_0010
				msg1 = toByte("00000010");
				break;
			case "AN" :
				//(angle)_0100
				msg1 = toByte(convertAngle(ms[1])+"0100");
				break;
			}
		}
		//msg1 = tobyte("00001000");	//�溸
	}
	
	public void start() {
		if(stateChecker == null)
			try {
				selectWebcam();
				acceptNewSocket();
				run();
				stateChecker = new Thread() {
					@Override
					public void run() {
						while(true) {
							if(androidSender.getState() == State.TERMINATED) {
								androidSender = null;
								sv.start();
								break;
							}
						}
					}
				};
				
				stateChecker.start();
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
	
	private synchronized void run () {
		androidSender = new Thread() {
			@Override
			public void run() {
				
				while(cinfo != null) {
					if(webcam == null) {
						selectWebcam(0);
					}
					//streaming webcam;
					long now = System.currentTimeMillis();
					try {
						if (now > last + delay) {
							BufferedImage img = webcam.getImage();
							if(img != null) cinfo.Imageout(img);
							else System.out.println("null image");
							System.out.println("send");
							last = now;
						}
					} catch(IOException e) {
						if(e.getClass().equals(SocketException.class)) {
							cinfo.dispose();
							cinfo = null;
							System.out.println("Socket Closed");
						}
						
					}
				}
				
			} 
		};
		
		androidSender.start();
	}
	private String convertAngle(String angle) {
		StringBuilder sb = new StringBuilder();
		int ang = 0;
		char[] ch = angle.toCharArray();
		for(int i = 0; i < ch.length; i++) ang += (ch[i]-'0') * Math.pow(10, i);
		if(ang == 0) sb.append(0);
		while(ang != 0) {
			sb.append(ang%2);
			ang /= 2;
		}
		
		return sb.reverse().toString();
	}
	private String convertSpeed(String speed) {
		String result = null;
		
		switch(speed) {
		case "0" : result = "001"; break;
		case "34" : result = "010"; break;
		case "68" : result = "100"; break;
		}
		
		return result;
	}
	private byte toByte(String msg) {
		byte b = 0;
		char[] ch = msg.toCharArray();
		for(int i = 0; i < ch.length; i++)	if(i > 7) break; else b += (ch[i]-'0') * Math.pow(2, i);
		return b;
	}
	
	public static void main(String[] args) {
		Server sv = new Server();
		sv.start();
	}
}