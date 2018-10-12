package testing;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Rasptest {

	private Socket sc;
	private boolean roof = true;
	
	public Rasptest() {
		try {
			Socket sct = new Socket("localhost",4240);
			sc = new Socket("localhost",4240);
			new Thread() {
				public void run() {
					while(roof) {
						try {
							sc.getInputStream().read();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sc.getOutputStream()));
			System.out.println("connected");
			
			Thread.sleep(1000);
			pw.println("MF:0");
			pw.flush();
			System.out.println("MF:0");
			
			Thread.sleep(3000);
			pw.println("ST:0");
			pw.flush();
			System.out.println("ST:");
			
			Thread.sleep(2000);
			pw.println("MB:0");
			pw.flush();
			System.out.println("MB:0");

			Thread.sleep(3000);
			pw.println("ST:0");
			pw.flush();
			System.out.println("ST:");
			
			Thread.sleep(2000);
			
			roof = false;
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Rasptest();
	}
}
