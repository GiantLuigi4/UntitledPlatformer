package block_properties;

import java.awt.*;

public class ColorProperty implements IBlockProperty {
	boolean isFocused=false;
	public int rgb=0xFFFFFF;
	@Override
	public void draw(Graphics2D g) {
		if (isFocused) {
			g.setColor(new Color(rgb).darker());
			g.fillRect(0,0,128,32);
			g.setColor(new Color(rgb));
		} else {
			g.setColor(new Color(rgb).darker().darker());
			g.fillRect(0,0,128,32);
			g.setColor(new Color(rgb).darker());
		}
		g.fillRect(1,1,126,30);
		g.setColor(new Color(0xFFFFFF));
		g.drawString(""+rgb,5,20);
	}
	
	@Override
	public void click(int mouseX, int mouseY) {
		isFocused=
				mouseX >= 0 && mouseX <= 128 &&
				mouseY >= 0 && mouseY <= 32;
	}
	
	@Override
	public void type(int character) {
		if (hasFocus()) {
			try {
				if (character==8) rgb=Integer.parseInt((""+rgb).substring(0,(""+rgb).length()-1));
				else rgb=Integer.parseInt((""+rgb)+((char)character));
			} catch (Throwable ignored) {
				if (character==8) rgb=0;
			}
		}
	}
	
	@Override
	public boolean hasFocus() {
		return isFocused;
	}
}
