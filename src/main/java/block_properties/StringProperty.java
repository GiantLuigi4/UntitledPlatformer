package block_properties;

import java.awt.*;

public class StringProperty implements IBlockProperty {
	boolean isFocused=false;
	public String text="";
	@Override
	public void draw(Graphics2D g) {
		if (isFocused) {
			g.setColor(new Color(0x313336).darker());
			g.fillRect(0,0,128,32);
			g.setColor(new Color(0x313336));
		} else {
			g.setColor(new Color(0x313336).darker().darker());
			g.fillRect(0,0,128,32);
			g.setColor(new Color(0x313336).darker());
		}
		g.fillRect(1,1,126,30);
		g.setColor(new Color(0xFFFFFF));
		g.drawString(text,5,20);
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
				System.out.println(character);
				if (character==8) text=(text.substring(0,text.length()-1));
				else if (character!=65535&&character!=10&&character!=127&&character!=27) text=((text)+((char)character));
			} catch (Throwable ignored) {
				if (character==8) text="";
			}
		}
	}
	
	@Override
	public boolean hasFocus() {
		return isFocused;
	}
}
