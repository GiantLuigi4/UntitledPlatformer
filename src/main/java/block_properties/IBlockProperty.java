package block_properties;

import java.awt.*;

public interface IBlockProperty {
	void draw(Graphics2D g);
	
	void click(int mouseX,int mouseY);
	
	void type(int character);
	
	boolean hasFocus();
}
