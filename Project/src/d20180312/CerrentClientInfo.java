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
 * 현재접속해있는 유저의 정보
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
			msg = in.readLine().trim();
		return msg;
	}  
	
	public synchronized void Imageout(BufferedImage img) throws IOException {
		/*
		byte[] imgBytes = ((DataBufferByte) img.getData().getDataBuffer()).getData();

		out.writeInt(img.getWidth());
		out.writeInt(img.getHeight());
		out.write(imgBytes);

		out.flush();
		/**/

		byte[] imgBytes = ((DataBufferByte) img.getData().getDataBuffer()).getData();
		byte[] imgbytes = new byte[img.getWidth() * img.getHeight() * 3 / 4];
		int index = 0;
		for(int i = 0; i < img.getHeight() / 2; i++)
			for(int j = 0; j < img.getWidth() / 2; j++)
				if(i % 2 == 1 && j % 2 == 1) {
					imgbytes[index] = imgBytes[index];
					imgbytes[index+1] = imgBytes[index+1];
					imgbytes[index+2] = imgBytes[index+2];
					index += 3;
				}

		out.writeInt(img.getWidth()/2);
		out.writeInt(img.getHeight()/2);
		out.write(imgbytes);

		out.flush();
		/**/
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
