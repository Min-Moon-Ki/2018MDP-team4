package d20180312;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * �����������ִ� ������ ����
 * @author admin
 */
public class CerrentClientInfo {
	
	private BufferedReader in;
	private DataOutputStream out;
	private Socket so;

 	public CerrentClientInfo(ServerSocket sv) {
		try {
	 		Socket temp = sv.accept();
			in = new BufferedReader(new InputStreamReader(temp.getInputStream()));
			out = new DataOutputStream(temp.getOutputStream());
			so = temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 	public CerrentClientInfo(Socket temp) {
		try {
			in = new BufferedReader(new InputStreamReader(temp.getInputStream()));
			out = new DataOutputStream(temp.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		so = temp;
	}
	
	public String in() throws IOException {
		String msg = null;
		if(in.ready())
			msg = (in.readLine()).trim();
		return msg;
	}  
	
	public synchronized void Imageout(BufferedImage img) throws IOException {
		byte[] imgBytes = ((DataBufferByte) img.getData().getDataBuffer()).getData();

		out.writeInt(img.getWidth());
		out.writeInt(img.getHeight());
		out.write(imgBytes);

		out.flush();
	}
	
	public boolean isclosed() {
		return so.isClosed();
	}
	
	public void dispose(){
		try {
			if(in != null)in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(out != null)out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}