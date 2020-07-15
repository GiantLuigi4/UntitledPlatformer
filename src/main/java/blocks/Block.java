package blocks;

import block_properties.ColorProperty;
import block_properties.IBlockProperty;
import game.Game;
import utils.MathUtils;

import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

public class Block {
	public int x;
	public int y;
	public int width;
	public int height;
	public int color;
	
	public Block(int x, int y, int width, int height, int color) {
		this.x = x-5;
		this.y = -y-5;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public Block(int x, int y, int width, int height, int color, boolean differentiator) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public Block newInstance(Integer x,Integer y,Integer width,Integer height,Integer color) {
		return new Block(x,y,width,height,color);
	}
	
	public void read(String s){}
	
	public Block(){}
	
	public void collide(long px,long py) {
		if (this.collidesPlayer(px,py)) {
			if (y+(height/2)<py) {
				Game.playerY+=1;
				py+=1;
				if (!this.collidesPlayer(px,py)) {
					Game.playerYMot=0;
				} else {
					if (Game.playerYMot>0) {
						Game.playerYMot-=1;
						Game.playerYMot= MathUtils.limit(2, Game.playerYMot,100);
					}
					Game.playerY-=1;
				}
			}
			if (y+(height/2)>py) {
				Game.playerY-=1;
				py-=1;
				if (!this.collidesPlayer(px,py)) {
					Game.onGround=true;
					Game.playerYMot=0;
				} else {
					if (Game.playerYMot>0) {
						Game.playerYMot-=1;
						Game.playerYMot= MathUtils.limit(2, Game.playerYMot,100);
					}
					Game.playerY+=1;
				}
			} else if (Game.playerYMot>0) {
				Game.playerYMot/=2;
				Game.playerYMot= MathUtils.limit(2, Game.playerYMot,100);
			}
		}
		if (this.collidesPlayer(px,py)) {
			if (x+(width/2)>px) {
				Game.playerX-=1;
			} else if (x+(width/2)<px) {
				Game.playerX+=1;
			}
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(new Color(color));
		g.fillRect(x,y,width,height);
	}
	
	public void drawCollision(Graphics2D g) {
		g.setColor(new Color(255,0,0,128));
		g.drawRect(x,y,width,height);
	}
	
	public void addProperties(ArrayList<IBlockProperty> properties) {
		properties.add(new ColorProperty());
	}
	
	public boolean collidesPlayer(long px,long py) {
		if (x-5<px&&px<x+width+5) {
			if (y-5<py&&py<y+height+5) {
				return true;
			}
		}
		return false;
	}
	
	public String getExtraInfo() {return "";}
	
	public boolean collides(long px,long py) {
		if (x<px&&px<x+width) {
			if (y<py&&py<y+height) {
				return true;
			}
		}
		return false;
	}
	
	public void readProperties(ArrayList<IBlockProperty> properties) {
		this.color=((ColorProperty)properties.get(0)).rgb;
	}
	
	public void update() {}
	
	public Block copy() {
		return new Block(x,y,width,height,color,true);
	}
}
