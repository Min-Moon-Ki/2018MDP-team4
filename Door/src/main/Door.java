package main;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;

import stand.communicate.Declearation;

public class Door {
	
	private ObjectOutputStream oos;
	private Webcam webcam;
	
	static {
		Webcam.setDriver(new FsWebcamDriver());
	}
	/**/
	Door() {
		try {
			Socket sc = new Socket("localhost",2580);
			oos = new ObjectOutputStream(sc.getOutputStream());
			
			webcam = Webcam.getDefault();
			webcam.setViewSize(WebcamResolution.VGA.getSize());
			webcam.open();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Door();
	}
	
	
}