package temp;

public class Direction {

	private static final double CA = 2, CB = 2; 
	private double angleACD, angleBCD;
	
	public static void main(String[] args) {
		Direction di = new Direction();
		double a,b,c;
		
		int x = 1, y = -2;
		
		a = Math.pow(1+x, 2) + Math.pow(1-y, 2) ;
		b = Math.pow(x, 2) + Math.pow(y, 2) ;
		c = Math.pow(1-x, 2) + Math.pow(1-y, 2) ;
		di.heading(a,b,c);
	}
	/**
	 *
	 * a = [-1,1]
	 * b = [1,1]
	 * c = [0,0]
	 * 
	 * d = ??
	 * 
	 * when only know distances, to know direction (from c)
	 * 
	 * @param ad
	 * Square of distance between a and d
	 * @param cd
	 * Square of distance between c and d
	 * @param bd
	 * Square of distance between b and d
	 */
	public void heading(double ad, double cd, double bd) {
		double cosACD,cosBCD;
		cosACD = (cd + CA - ad) / (2 * Math.sqrt(cd) * Math.sqrt(CA));
		cosBCD = (cd + CB - bd) / (2 * Math.sqrt(cd) * Math.sqrt(CB));
		angleACD = (Math.acos(cosACD)/Math.PI)*180;
		angleBCD = (Math.acos(cosBCD)/Math.PI)*180;
		System.out.println(angleACD);
		System.out.println(angleBCD);
	}
}