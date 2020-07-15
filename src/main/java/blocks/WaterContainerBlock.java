package blocks;

import java.awt.Graphics2D;

public class WaterContainerBlock extends Block {
	public WaterContainerBlock(int x, int y, int width, int height, int color) {
		super(x, y, width, height, color);
	}
	
	public WaterContainerBlock(int x, int y, int width, int height, int color, boolean differentiator) {
		super(x, y, width, height, color, differentiator);
	}
	
	@Override public void collide(long px, long py){}@Override public void draw(Graphics2D g){}@Override public void drawCollision(Graphics2D g){}@Override public boolean collidesPlayer(long px, long py){return false;}@Override public void update(){}@Override public Block copy() {return new WaterContainerBlock(x,y,width,height,color,true);}public WaterContainerBlock(){}
	@Override public boolean collides(long px, long py) {
		boolean withinX=x-1<px&&px<x+1+width;
		boolean withinY=y-1<py&&py<y+1+height;
		boolean collidesTop=withinX&&py==y-1;
		boolean collidesBottom=withinX&&py==y+1+height;
		boolean collidesLeft=withinY&&px==x-1;
		boolean collidesRight=withinY&&px==x+1+width;
		return collidesTop||collidesBottom||collidesLeft||collidesRight;
	}
	
	@Override public Block newInstance(Integer x, Integer y, Integer width, Integer height, Integer color) {return new WaterContainerBlock(x, y, width, height, color);}
}
