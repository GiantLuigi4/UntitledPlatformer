package block_properties;

import java.awt.*;

public class IntegerProperty implements IBlockProperty {
	boolean isFocused=false;
	public int number=0;
	private boolean isNegative=false;
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
		g.drawString((isNegative?"-":"")+ number,5,20);
	}
	
	@Override
	public void click(int mouseX, int mouseY) {
		isFocused=
				mouseX >= 0 && mouseX <= 128 &&
				mouseY >= 0 && mouseY <= 32;
	}
	
	public int getNumber() {
		return number*(isNegative?-1:1);
	}
	
	@Override
	public void type(int character) {
		if (hasFocus()) {
			try {
				if (character==8) number=Integer.parseInt((""+number).substring(0,(""+number).length()-1));
				else if (character==45) isNegative=!isNegative;
				else number=Integer.parseInt((""+number)+((char)character));
			} catch (Throwable ignored) {
				if (character==8) number=0;
			}
		}
	}
	
	@Override
	public boolean hasFocus() {
		return isFocused;
	}
}
