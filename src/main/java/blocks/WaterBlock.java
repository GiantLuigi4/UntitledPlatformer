package blocks;

import game.Game;
import utils.ImageUtils;
import utils.MathUtils;


import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class WaterBlock extends Block implements IBackgroundBlock {
	final Color waterColor=new Color(color);
	private BufferedImage bimig=ImageUtils.setupBimig(x,y,width,height,new Color(waterColor.getRed(),waterColor.getBlue(),waterColor.getGreen(),MathUtils.limit(0,waterColor.getAlpha(),64)).getRGB(),Game.waterImage,null);
	
	public WaterBlock(int x, int y, int width, int height, int color, boolean differentiator) {
		super(x, y, width, height, color, differentiator);
		Color c=new Color(color);
//		waterColor=new Color(c.getRed(),c.getGreen(),c.getBlue(),64);
	}
	
	public WaterBlock(int x, int y, int width, int height, int color) {
		super(x, y, width, height, color);
		Color c=new Color(color);
//		waterColor=new Color(c.getRed(),c.getGreen(),c.getBlue(),64);
	}
	
	@Override
	public void collide(long px, long py) {
		if (x-5<px&&px<x+width+5) {
			if (y-5<py&&py<y+height+5) {
				Game.onGround=true;
				Game.playerYMot= MathUtils.limit(-4, Game.playerYMot,4);
			}
		}
	}
	
	public WaterBlock(){/*waterColor=null;*/}
	
	@Override
	public Block newInstance(Integer x, Integer y, Integer width, Integer height, Integer color) {
		return new WaterBlock(x, y, width, height, color);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (justSized||checkFlow) {
			bimig=ImageUtils.setupBimig(x,y,width,height,new Color(waterColor.getRed(),waterColor.getBlue(),waterColor.getGreen(),MathUtils.limit(0,waterColor.getAlpha(),64)).getRGB(),Game.waterImage,null);
		}
		g.drawImage(bimig,x,y,null);
	}
	
	@Override
	public void drawBackground(Graphics2D g) {
		super.draw(g);
	}
	
	@Override
	public boolean collides(long x, long y) {
		return super.collides(x,y);
	}
	
	boolean checkFlow=true;
	boolean justSized=true;
	
	int prevX=x;
	
	@Override
	public void update() {
		justSized=false;
		prevX=x;
		super.update();
		if (checkFlow) {
			boolean flow=false;
			if (
					Game.getBlock(x+1,y+height)==null&&
					Game.getBlock(x+width-1,y+height)==null
			) {
				this.height+=1;
				flow=true;
			}
			if (
					Game.getBlock(x-1,y+1)==null&&
					Game.getBlock(x-1,y+height-1)==null
			) {
				this.x-=1;
				this.width+=1;
				flow=true;
			}
			if (
					Game.getBlock(x+width+1,y+1)==null&&
					Game.getBlock(x+width+1,y+height-1)==null
			) {
				this.width+=1;
				flow=true;
			}
			checkFlow=flow;
			int size=10;
			if (this.height>size) {
				int nextWaterHeight=this.height-size;
				this.height=size;
				Game.game.blocksToAdd.add(new WaterBlock(x,y+size,width,nextWaterHeight,color,true));
			}
			if (this.width>size) {
				if (this.x==(((this.x)/size)*size)) {
					int nextWaterWidth=this.width-size;
					this.width=size;
					Game.game.blocksToAdd.add(new WaterBlock(x+size,y,nextWaterWidth,height,color,true));
				}
			}
			justSized=true;
		} else {
			Block bk= Game.getBlock(x+width+1,y+height/2);
			Block bk3= Game.getBlock(x+width/2,y+height+1);
			if (
					bk!=null&&
					this.height==bk.height&&
					bk instanceof WaterBlock&&
					!((WaterBlock) bk).checkFlow&&
					!((WaterBlock) bk).justSized&&
					bk.y==this.y
			) {
				if (bk!=this) {
					if (!Game.blocksToRemove.contains(bk)) {
						Game.blocksToRemove.add(bk);
						int add=(bk.width/64);
						for (int i=0;i<64;i++) {
							Block bk2= Game.getBlock(x+width+add+1,y+height/2);
							if (bk2==null) {
								this.width+=(add);
							}
						}
					}
				}
			}if (
					bk3!=null&&
					this.width==bk3.width&&
					bk3 instanceof WaterBlock&&
					!((WaterBlock) bk3).checkFlow&&
					!((WaterBlock) bk3).justSized&&
					bk3.x==this.x
			) {
				if (bk3!=this) {
					if (!Game.blocksToRemove.contains(bk3)) {
						Game.blocksToRemove.add(bk3);
						int add=(bk3.height/1);
//						for (int i=0;i<4;i++) {
//							Blocks.Block bk2=game.Game.getBlock(x+height+add+1,y+height/2);
//							if (bk2==null) {
								this.height+=(add);
//							}
//						}
					}
				}
			} else {
				if (Game.getBlock(x-1,y+height/2)==null) {
					this.x-=1;
					this.width+=1;
				} else {
					this.x+=1;
					this.width-=1;
					if (Game.getBlock(x-1,y+height/2)==null) {
						this.x-=1;
						this.width+=1;
					}
				}
				if (Game.getBlock(x+width+1,y+height/2)==null) {
					this.width+=1;
				} else {
					this.width-=1;
					if (Game.getBlock(x+width+1,y+height/2)==null) {
						this.width+=1;
					}
				}
				if (
						Game.getBlock(x+1,y+height)instanceof WaterBlock||
						Game.getBlock(x+width/2,y+height)instanceof WaterBlock||
						Game.getBlock(x+width-1,y+height)instanceof WaterBlock
				) {
					this.height-=1;
				}
			}
		}
		
		Block bk1= Game.getBlock(x+width-1,y+height/2);
		if (bk1!=this&&!Game.blocksToRemove.contains(bk1)) {
			Game.blocksToRemove.add(this);
		}
		Block bk2= Game.getBlock(x+1,y+height/2);
		if (bk2!=this&&!Game.blocksToRemove.contains(bk2)) {
			Game.blocksToRemove.add(this);
		}
	}
	
	@Override
	public void drawCollision(Graphics2D g) {
		g.setColor(new Color(0,255,0,255));
		g.drawRect(x,y,width,height);
	}
	
	@Override
	public Block copy() {
		return new WaterBlock(x,y,width,height,color,true);
	}
}
