package rooms;

import blocks.Block;
import blocks.IRoomRenderable;

import java.awt.*;
import java.util.ArrayList;

public class Room {
	final RoomTemplate template;
	final int x;
	final int y;
	boolean hasGenerated=false;
	
	public Room(RoomTemplate template, int x, int y) {
		this.template = template;
		this.x = x;
		this.y = y;
	}
	
	public int getMinX() {
		return template.minX+x;
	}
	
	public int getMaxX() {
		return template.maxX+x;
	}
	
	public int getMinY() {
		return template.minY-y;
	}
	
	public int getMaxY() {
		return template.maxY-y;
	}
	
	public void add(ArrayList<Block> blocks) {
		if (!hasGenerated) {
			if (blocks.isEmpty()) template.add(this.blocks,x,y);
//			template.add(blocks,x,y);
			blocks.addAll(this.blocks);
		}
		hasGenerated=true;
	}
	
	ArrayList<Block> blocks=new ArrayList<>();
	public void draw(Graphics2D g) {
		g.setColor(new Color(0x313336));
		int height=this.getMaxY()-this.getMinY();
		g.fillRect(this.getMinX(),this.getMinY(),this.getMaxX()-this.getMinX(),height);
		if (!hasGenerated) {
			if (blocks.isEmpty()) template.add(blocks,x,y);
			blocks.forEach(b->{
				b.draw(g);
				if (b instanceof IRoomRenderable) {
					((IRoomRenderable) b).drawInRoom(g);
				}
			});
		}
	}
	
	public boolean containsPoint(long x,long y) {
		if (
				template.minX+this.x<x&&x<template.maxX+this.x&&
				template.minY-this.y<y&&y<template.maxY-this.y
		) {
			return true;
		}
		return false;
	}
	
	public boolean collidesWithRoom(Room that) {
		int minX1=this.getMinX();
		int minX2=that.getMinX();
		int minY1=this.getMinY();
		int minY2=that.getMinY();
		int maxX1=this.getMaxX();
		int maxX2=that.getMaxX();
		int maxY1=this.getMaxY();
		int maxY2=that.getMaxY();
		//TODO: Remove java.awt.rectangle usage
		if (
				new Rectangle(minX1,minY1,maxX1-minX1,maxY1-minY1).intersects(
					new Rectangle(minX2,minY2,maxX2-minX2,maxY2-minY2)
				)
		) {
			return true;
		}
		return false;
	}
}
