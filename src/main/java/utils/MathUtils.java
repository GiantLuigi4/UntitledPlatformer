package utils;

public class MathUtils {
	public static double lerp(double pct, double start, double end) {
		return (((pct)*start)+((1-pct)*end));
	}
	
	public static long limit(long min, long value, long max) {
		return Math.min(max,Math.max(min,value));
	}
	
	public static double limit(double min, double value, double max) {
		return Math.min(max,Math.max(min,value));
	}
	
	public static int limit(int min, int value, int max) {
		return Math.min(max,Math.max(min,value));
	}
}
