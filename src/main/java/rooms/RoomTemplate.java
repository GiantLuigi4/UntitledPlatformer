package rooms;

import blocks.Block;
import blocks.WaterContainerBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomTemplate {
	private final List<Block> thisBlocks;
	
	int minX=0;
	int maxX=0;
	int minY=0;
	int maxY=0;
	
	public RoomTemplate(Block... blocks) {
		this(Arrays.asList(blocks));
	}
	
	public RoomTemplate(List<Block> blocks) {
		thisBlocks=blocks;
		blocks.forEach((block -> {
			if (block.x<minX) {
				minX=block.x;
			}
			if (block.y<minY) {
				minY=block.y;
			}
			if (block.x+block.width>maxX) {
				maxX=block.x+block.width;
			}
			if (block.y+block.height>maxY) {
				maxY=block.y+block.height;
			}
		}));
	}
	
	public void add(ArrayList<Block> blocks, int x, int y) {
		thisBlocks.forEach((b)->{
			Block b1=b.copy();
			b1.x+=x;
			b1.y-=y;
			blocks.add(b1);
		});
		blocks.add(new WaterContainerBlock(minX+x,minY-y,(maxX-minX),(maxY-minY),0,true));
	}
}
