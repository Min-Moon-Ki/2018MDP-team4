package d20180312;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.github.sarxos.webcam.Webcam;

public class test {
	
	private Webcam webcam;
	
	public test() {
		try {
			Socket sc = new Socket("localhost",4240);
			DataInputStream in = new DataInputStream(sc.getInputStream());
			
			while(true) {
				int w = in.readInt();
				int h = in.readInt();

				byte[] imgBytes = new byte[w * h * 3]; // 3 byte RGB
				in.readFully(imgBytes);

				// Convert 4 byte interleaved ABGR to int packed ARGB
				int[] pixels = new int[w * h];
				for (int i = 0; i < pixels.length; i++) {
					int byteIndex = i * 3;
					pixels[i] =
							((imgBytes[byteIndex    ] & 0xFF) << 16)
									| ((imgBytes[byteIndex + 1] & 0xFF) <<  8)
									|  (imgBytes[byteIndex + 2] & 0xFF);
				}
				System.out.println("clear");
			}
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if(e.getClass().equals(SocketException.class)) {
				System.out.println("disconnected");
			}
		}
	}
	public static void main(String[] args) {
		new test();
	}
}
