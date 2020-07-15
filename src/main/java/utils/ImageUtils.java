package utils;

import game.Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtils {
	public static BufferedImage setupBimig(int x, int y, int width, int height, int color, BufferedImage toTile,BufferedImage border) {
		if (width>0&&height>0) {
			BufferedImage bim=new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d=(Graphics2D)bim.getGraphics();
			int xPos=Math.abs(Math.abs(x)%toTile.getWidth());
			int yPos=Math.abs(Math.abs(y)%toTile.getHeight());
			Color c=new Color(color,false);
			for (int xIMGPos=xPos-toTile.getWidth();xIMGPos<width;xIMGPos+=toTile.getWidth()) {
				for (int yIMGPos=yPos-toTile.getHeight();yIMGPos<height;yIMGPos+=toTile.getHeight()) {
					for (int xIMGRead=0;xIMGRead<toTile.getWidth();xIMGRead++) {
						for (int yIMGRead=0;yIMGRead<toTile.getHeight();yIMGRead++) {
							Color c1=new Color(toTile.getRGB(xIMGRead,yIMGRead),true);
							g2d.setColor(new Color(
									(int)MathUtils.limit(0,c.getRed()*(c1.getRed()/255f),255),
									(int)MathUtils.limit(0,c.getGreen()*(c1.getGreen()/255f),255),
									(int)MathUtils.limit(0,c.getBlue()*(c1.getBlue()/255f),255),
									(int)MathUtils.limit(0,c.getAlpha()*(c1.getAlpha()/255f),255)
							));
							if (border!=null) {
//								if (
//										(y==(y+yIMGPos+yIMGRead)&&Game.getBlock(x+xIMGPos+xIMGRead,y-height/2)==null)
//								) {
//									c1=new Color(border.getRGB(xIMGRead,yIMGRead),true);
//									g2d.setColor(new Color(
//											(int)MathUtils.limit(0,c.getRed()*(c1.getRed()/255f),255),
//											(int)MathUtils.limit(0,c.getGreen()*(c1.getGreen()/255f),255),
//											(int)MathUtils.limit(0,c.getBlue()*(c1.getBlue()/255f),255),
//											(int)MathUtils.limit(0,c.getAlpha()*(c1.getAlpha()/255f),255)
//									));
//								}
							}
							g2d.fillRect(xIMGPos+xIMGRead,yIMGPos+yIMGRead,1,1);
						}
					}
				}
			}
			return bim;
		} else {
			return null;
		}
	}
}
