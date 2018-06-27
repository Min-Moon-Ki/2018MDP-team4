package d20180312;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class TestServer {

	private Webcam webcam;
	private DataOutputStream out;
	private Socket temp;
	private CerrentClientInfo cinfo;
	private Thread th;

	private double frame = 60d;
	private long last = -1;
	private long delay = (long)(1000/frame);
	TestServer() {
		try {
			ServerSocket sv = new ServerSocket(4240);
			cinfo = new CerrentClientInfo(sv);
			selectWebcam(0);
			
			th = new Thread() {
				@Override
				public void run() {
					
					while(cinfo != null) {
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
			th.start();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void selectWebcam(int select) {
		List<Webcam> wclist = Webcam.getWebcams();
		if(wclist.isEmpty()) webcam = Webcam.getDefault();
		else webcam = wclist.get(select);
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();
		System.out.println("cam");
	}
	
	public static void main(String[] args) {
		new TestServer();
	}
}