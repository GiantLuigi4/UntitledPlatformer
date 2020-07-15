package rooms;

import blocks.Block;
import blocks.IRoomRenderable;
import blocks.WaterContainerBlock;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RoomTemplateEditor {
	public ArrayList<Block> thisBlocks;
	public ArrayList<Block> renderBlocks;
	public ArrayList<Block> threadsafeThisBlocks;
	
	int minX=0;
	int maxX=0;
	int minY=0;
	int maxY=0;
	
	public RoomTemplateEditor(Block... blocks) {
		this(Arrays.asList(blocks));
	}
	
	public RoomTemplateEditor(ArrayList<Block> blocks) {
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
		threadsafeThisBlocks=(ArrayList<Block>)thisBlocks.clone();
	}
	
	public RoomTemplateEditor(List<Block> blocks) {
		thisBlocks=new ArrayList<>();
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
			thisBlocks.add(block);
		}));
		renderBlocks=new ArrayList<>();
		threadsafeThisBlocks=(ArrayList<Block>)thisBlocks.clone();
	}
	
	public void render(Graphics2D g,boolean drawCollision) {
		if (renderBlocks.isEmpty()) renderBlocks.addAll(threadsafeThisBlocks);
		if (drawCollision) {
			try {
				((ArrayList<Block>)renderBlocks.clone()).forEach(block->{
					block.draw(g);
					if (block instanceof IRoomRenderable) {
						((IRoomRenderable) block).drawInRoom(g);
					}
					block.drawCollision(g);
				});
			} catch (ConcurrentModificationException err) {
				if (renderBlocks.isEmpty()) renderBlocks.addAll(thisBlocks);
				((ArrayList<Block>)renderBlocks.clone()).forEach(block->{
					block.draw(g);
					if (block instanceof IRoomRenderable) {
						((IRoomRenderable) block).drawInRoom(g);
					}
					block.drawCollision(g);
				});
			}
		} else {
			try {
				((ArrayList<Block>)renderBlocks.clone()).forEach(block->{
					block.draw(g);
					if (block instanceof IRoomRenderable) {
						((IRoomRenderable) block).drawInRoom(g);
					}
				});
			} catch (ConcurrentModificationException err) {
				if (renderBlocks.isEmpty()) renderBlocks.addAll(thisBlocks);
				((ArrayList<Block>)renderBlocks.clone()).forEach(block->{
					block.draw(g);
					if (block instanceof IRoomRenderable) {
						((IRoomRenderable) block).drawInRoom(g);
					}
				});
			}
		}
	}
	
	public void add(ArrayList<Block> blocks, int x, int y) {
		threadsafeThisBlocks.forEach((b)->{
			Block b1=b.copy();
			b1.x+=x;
			b1.y-=y;
			blocks.add(b1);
		});
		blocks.add(new WaterContainerBlock(minX+x,minY-y,(maxX-minX),(maxY-minY),0,true));
	}
	
	public void addBlock(Block b) {
		if (
				!hasBlockAt(b.x,b.y)&&
				!hasBlockAt(b.x+b.width,b.y)&&
				!hasBlockAt(b.x+b.width,b.y+b.height)&&
				!hasBlockAt(b.x+b.width/2,b.y+b.height/2)&&
				!hasBlockAt(b.x+b.width/2,b.y+b.height)&&
				!hasBlockAt(b.x+b.width,b.y+b.height/2)&&
				!hasBlockAt(b.x+b.width/2,b.y)&&
				!hasBlockAt(b.x,b.y+b.height/2)&&
				!hasBlockAt(b.x,b.y+b.height)
		) {
			while (mergeBlocks(b));
			thisBlocks.add(b.copy());
		}
		renderBlocks=new ArrayList<>();
		threadsafeThisBlocks=(ArrayList<Block>)thisBlocks.clone();
	}
	
	private boolean mergeBlocks(Block b) {
		boolean merged=false;
		Block b2=this.getBlockAt(b.x+b.width+1,b.y+b.height/2);
		if (
				b2!=null&&
				b2!=b&&
				b2.getClass().equals(b.getClass())&&
				b2.height==b.height&&
				b2.y==b.y
		) {
			this.removeBlock(b2.x+b2.width/2,-(b2.y+b2.height/2));
			b.width+=b2.width;
			merged=true;
		}
		b2=this.getBlockAt(b.x-1,b.y+b.height/2);
		if (
				b2!=null&&
				b2!=b&&
				b2.getClass().equals(b.getClass())&&
				b2.height==b.height&&
				b2.y==b.y
		) {
			this.removeBlock(b2.x+b2.width/2,-(b2.y+b2.height/2));
			b.width+=b2.width;
			b.x=b2.x;
			merged=true;
		}
		b2=this.getBlockAt(b.x+b.width/2,b.y+b.height+1);
		if (
				b2!=null&&
				b2!=b&&
				b2.getClass().equals(b.getClass())&&
				b2.width==b.width&&
				b2.x==b.x
		) {
			this.removeBlock(b2.x+b2.width/2,-(b2.y+b2.height/2));
			b.height+=b2.height;
			merged=true;
		}
		b2=this.getBlockAt(b.x+b.width/2,b.y-1);
		if (
				b2!=null&&
				b2!=b&&
				b2.getClass().equals(b.getClass())&&
				b2.width==b.width&&
				b2.x==b.x
		) {
			this.removeBlock(b2.x+b2.width/2,-(b2.y+b2.height/2));
			b.height+=b2.height;
			b.y=b2.y;
			merged=true;
		}
		return merged;
	}
	
	public void removeBlock(int x,int y) {
		try {
			thisBlocks.forEach(block -> {
				if (block.collides(x,-y)) thisBlocks.remove(block);
			});
		} catch (Throwable ignored) {}
		renderBlocks.clear();
		threadsafeThisBlocks=(ArrayList<Block>)thisBlocks.clone();
	}
	
	public boolean hasBlockAtFlipY(int x, int y) {
		AtomicBoolean atomicBoolean=new AtomicBoolean(false);
		thisBlocks.forEach(block -> {
			if (block.collides(x,-y)) atomicBoolean.set(true);
		});
		return atomicBoolean.get();
	}
	
	public boolean hasBlockAt(int x,int y) {
		AtomicBoolean atomicBoolean=new AtomicBoolean(false);
		thisBlocks.forEach(block -> {
			if (block.collides(x,y)) atomicBoolean.set(true);
		});
		return atomicBoolean.get();
	}
	
	public Block getBlockAt(int x,int y) {
		AtomicReference<Block> b=new AtomicReference<>(null);
		thisBlocks.forEach(block -> {
			if (block.collides(x,y)) b.set(block);
		});
		return b.get();
	}
}
