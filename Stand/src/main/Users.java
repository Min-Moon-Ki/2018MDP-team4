package main;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.imageio.ImageIO;

import stand.communicate.Declearation;

public class Users {

	private InputStream inp;
	private String name;
	
	Users(Socket soc, String name) {
		try {
			inp = soc.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.name = name;
	}

	public Declearation readDeclear() {
		Declearation dec = null;
		
		//make Declearation
		{
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inp));
			try {
				//add parts
				String[] parts = br.readLine().split(","); 
				
				if(parts.length != 3) return null;
				
				dec = new Declearation(parts);
				
				BufferedImage img = ImageIO.read(inp);
				dec.setImage(img);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return dec;
	}
	
	public boolean isName(String name) {
		return this.name == name;
	}
}
