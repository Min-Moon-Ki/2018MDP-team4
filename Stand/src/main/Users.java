package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Users {

	private ObjectInputStream ois;
	private String name;
	
	Users(Socket soc, String name) {
		try {
			ois = new ObjectInputStream(soc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.name = name;
	}
	public ObjectInputStream getOIS() {
		return ois;
	}
	
	public boolean isName(String name) {
		return this.name == name;
	}
}