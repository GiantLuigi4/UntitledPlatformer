package blocks;

import block_properties.IBlockProperty;
import game.Game;
import utils.ImageUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlockBrick extends Block {
	private BufferedImage bimig=ImageUtils.setupBimig(x,y,width,height,color,Game.brickOneImage,Game.brickOneImageBorder);
	public BlockBrick(int x, int y, int width, int height, int color) {
		super(x, y, width, height, color);
	}
	
	public BlockBrick(int x, int y, int width, int height, int color, boolean differentiator) {
		super(x, y, width, height, color, differentiator);
	}
	
	@Override
	public void readProperties(ArrayList<IBlockProperty> properties) {
		super.readProperties(properties);
		bimig=ImageUtils.setupBimig(x,y,width,height,color,Game.brickOneImage,Game.brickOneImageBorder);
	}
	
	@Override
	public Block copy() {
		return new BlockBrick(x,y,width,height,color,true);
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bimig,x,y,null);
	}
	
	@Override
	public Block newInstance(Integer x, Integer y, Integer width, Integer height, Integer color) {
		return new BlockBrick(x, y, width, height, color);
	}
	
	public BlockBrick(){
		this.x=0;
		this.y=0;
		this.width=0;
		this.height=0;
		this.color=0;
	}
}
