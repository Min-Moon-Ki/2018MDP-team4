package temp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class Transmitter {

	private Webcam webcam;
	private boolean loop = false;
	private BufferedOutputStream bos;
	
	Transmitter() {
		ArrayList<Webcam> wclist = (ArrayList<Webcam>) Webcam.getWebcams();
		webcam = wclist.get(0);
		webcam.setViewSize(WebcamResolution.VGA.getSize());
	}
	
	public void openServer(int port) {
		try {
			ServerSocket server = new ServerSocket(port);
			Socket sc = server.accept();
			bos = new BufferedOutputStream(sc.getOutputStream());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void s() {
		loop = false;
	}
	public void run() {
		try{
			loop = true;
			while(loop) {
				ImageIO.write(webcam.getImage(), "JPG", bos);
				bos.flush();
				Thread.sleep(16);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
