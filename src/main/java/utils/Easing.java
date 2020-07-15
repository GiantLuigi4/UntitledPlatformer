package utils;

public class Easing {
	//https://github.com/bernie-g/geckolib/blob/094d60de2548890cba03202cdd40a2b2061a03ba/src/main/java/software/bernie/geckolib/easing/EasingManager.java#L76-L89
	public static double easeInSine(double x) {
		return 1 - Math.cos((float) ((x * Math.PI) / 2));
	}
	
	public static double easeOutSine(double x) {
		return Math.sin((float) ((x * Math.PI) / 2));
	}
	
	public static double easeInOutSine(double x) {
		return -(Math.cos((float) (Math.PI * x)) - 1) / 2;
	}
}
