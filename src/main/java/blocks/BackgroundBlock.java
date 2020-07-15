package blocks;


import java.awt.Graphics2D;
import java.awt.Color;

public class BackgroundBlock extends Block {
	public BackgroundBlock(int x, int y, int width, int height, int color) {
		super(x, y, width, height, new Color(color).darker().getRGB());
	}
	
	public BackgroundBlock(int x, int y, int width, int height, int color, boolean differentiator) {
		super(x, y, width, height, color, differentiator);
	}
	
	@Override public void collide(long px, long py) {}
	@Override public void drawCollision(Graphics2D g) {}
	
	@Override
	public boolean collidesPlayer(long px, long py) {
		return false;
	}
	
	@Override
	public Block newInstance(Integer x, Integer y, Integer width, Integer height, Integer color) {
		return new BackgroundBlock(x, y, width, height, color);
	}
	
	public BackgroundBlock(){}
	
	@Override
	public boolean collides(long px, long py) {
		return false;
	}
	
	@Override
	public Block copy() {
		return new BackgroundBlock(x,y,width,height,color,true);
	}
}
