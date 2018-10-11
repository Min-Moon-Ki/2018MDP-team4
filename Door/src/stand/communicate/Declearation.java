package stand.communicate;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Declearation implements Serializable {

	private static final long serialVersionUID = -5904255892936942457L;
	private String name;
	private String location;
	private String time;
	private BufferedImage img;
	
	Declearation (String[] parts) {
		this(parts[0],parts[1],parts[2]);
	}
	Declearation (String name, String location, String time) {
		this.name = name;
		this.location = location;
		this.time = time;
	}
	
	public String[] getInfo() {
		String[] temp = {name, location, time};
		return temp;
	}
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
