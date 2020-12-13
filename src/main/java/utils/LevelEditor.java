package utils;

import block_properties.IBlockProperty;
import blocks.Block;
import blocks.BlockRegistry;
import blocks.IEditorRenderable;
import blocks.IRoomRenderable;
import game.Game;
import rooms.Room;
import rooms.RoomTemplateEditor;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class LevelEditor extends Game {
	private int mx=0;
	private int my=0;
	
	int editorMX=0;
	int editorMY=0;
	
	private final ArrayList<IBlockProperty> properties=new ArrayList<>();
	
	private static final LevelEditor instance=new LevelEditor();
	
	public static final String elementPaneTab="lever_editor_element_object_pane_tab_thing.png";
	public static final File elementPaneTabFile=new File(gameDir+"\\assets\\"+elementPaneTab);
	private static boolean allowPlacement=true;
	public static final String buttonUnpressed="leervel_editor_button_unpressed.png";
	public static final File buttonUnpressedFile=new File(gameDir+"\\assets\\"+buttonUnpressed);
	public static final String buttonPressed="leervel_editor_button_yespressed.png";
	public static final File buttonPressedFile=new File(gameDir+"\\assets\\"+buttonPressed);
	
	boolean isHoveringElementPane=false;
	boolean isHoveringElementPaneRight=false;
	int timeSinceHover=0;
	int timeSinceHoverRight=0;
	
	RoomTemplateEditor template=new RoomTemplateEditor();
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		AffineTransform transform=g2d.getTransform();
		super.paint(g);
		int gridSize=16;
		if (grid) {
			for (int x=-gridSize;x<=gridSize;x++) {
				for (int y=-gridSize;y<=gridSize;y++) {
					if (x==0||y==0) {
						g.setColor(new Color(0,255, 0, 128));
					} else {
						g.setColor(new Color(255,255,255, 128));
					}
					AffineTransform transform1=g2d.getTransform();
					g2d.translate((x*10)-5,(y*10)-5);
					g2d.scale(0.25,0.25);
					g.drawRect(1,1,38,38);
					g.setColor(new Color(0x80000000, true));
					g.drawRect(0,0,40,40);
					g2d.setTransform(transform1);
				}
			}
		}
		template.render(g2d,collisionOutlines);
		
		try {
			boolean hasBlock=template.hasBlockAtFlipY(editorMX,editorMY);
			Class<? extends Block> blockClassPlace=BlockRegistry.blocks.get(selectedBlockType);
			Block preview=(Block)blockClassPlace.getMethod("newInstance", Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).invoke(blockClassPlace.newInstance(),editorMX,editorMY,10,10,new Color(255,!hasBlock?255:0,!hasBlock?255:0,128).getRGB());
			preview.readProperties(properties);
			if (preview instanceof IEditorRenderable) {
				((IEditorRenderable) preview).drawInEditor(g2d);
			}
			if (preview instanceof IRoomRenderable) {
				((IRoomRenderable) preview).drawInRoom(g2d);
				preview.drawCollision(g2d);
			}
			preview.draw(g2d);
		} catch (Throwable err) {}
		
		g2d.setTransform(transform);
		
		g2d.setColor(new Color(0));
		try {
			BufferedImage image=ImageIO.read(elementPaneTabFile);
			if (mx<=image.getWidth()||(isHoveringElementPane&&mx<=image.getWidth()+timeSinceHover)) {
				timeSinceHover++;
				timeSinceHover=MathUtils.limit(0,timeSinceHover,64);
				isHoveringElementPane=true;
			} else {
				isHoveringElementPane=false;
				timeSinceHover--;
				timeSinceHover=MathUtils.limit(0,timeSinceHover,64);
			}
			g2d.drawImage(image,timeSinceHover,frame.getHeight()/2-image.getHeight()/2,null);
			g2d.setColor(new Color(38,38,38));
			g2d.fillRect(timeSinceHover-64,frame.getHeight()/2-image.getHeight()/2,64,image.getHeight());
			for (int i=0;i<8;i++) {
				float f=i/8f;
				g2d.setColor(new Color(
						(int)MathUtils.lerp(f,38,16),
						(int)MathUtils.lerp(f,38,16),
						(int)MathUtils.lerp(f,38,16)
				));
				g2d.fillRect(timeSinceHover-i,frame.getHeight()/2-image.getHeight()/2,1,image.getHeight());
			}
			
			AtomicInteger yPos= new AtomicInteger(-2);
			BlockRegistry.blocks.forEach(blockClass->{
				AffineTransform transform1=g2d.getTransform();
				try {
					Block b=(Block)blockClass.getMethod("newInstance", Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).invoke(blockClass.newInstance(),0,0,40,40,new Color(255,255,255,255).getRGB());
					g2d.translate(0,frame.getHeight()/2+yPos.get()*50);
					g2d.translate(-64+timeSinceHover,-40);
//					g2d.scale(4,4);
					g2d.translate(8*2,0);
					g2d.setColor(new Color(0,0,0));
					g2d.fillRect(-5,-5,39,39);
					if (hoveredBlockType==(yPos.get())+2) {
						g2d.setColor(new Color(0,0,255));
						g2d.drawRect(-6,-6,41,41);
						g2d.drawRect(-7,-7,43,43);
					}
					if (selectedBlockType==(yPos.get())+2) {
						g2d.setColor(new Color(0,255,0));
						g2d.drawRect(-6,-6,41,41);
						g2d.drawRect(-7,-7,43,43);
					}
					g2d.setColor(new Color(255,0,0));
					g2d.drawRect(-5,-5,39,39);
					b.draw(g2d);
					yPos.getAndIncrement();
				} catch (Throwable err) {
//					err.printStackTrace();
				}
				g2d.setTransform(transform1);
			});
		} catch (Throwable ignored) {}
		
		try {
			AffineTransform source=g2d.getTransform();
			
			int tabSize=256;
			
			int xTabPos=frame.getWidth()-(62+tabSize);
			g2d.translate(xTabPos,0);
			g2d.translate(46,0);
			BufferedImage image = ImageIO.read(elementPaneTabFile);
			if (mx <= xTabPos + 16 || (!isHoveringElementPaneRight && mx <= 16 + xTabPos + timeSinceHoverRight)) {
				timeSinceHoverRight++;
				timeSinceHoverRight = MathUtils.limit(0, timeSinceHoverRight, tabSize);
				isHoveringElementPaneRight = false;
			} else {
				isHoveringElementPaneRight = true;
				timeSinceHoverRight--;
				timeSinceHoverRight = MathUtils.limit(0, timeSinceHoverRight, tabSize);
			}
			
			AffineTransform transform1=g2d.getTransform();
			g2d.scale(-1,1);
			g2d.drawImage(image, -timeSinceHoverRight, frame.getHeight() / 2 - image.getHeight() / 2, null);
			g2d.setTransform(transform1);
			
			g2d.setColor(new Color(38, 38, 38));
			g2d.fillRect(timeSinceHoverRight, frame.getHeight() / 2 - image.getHeight() / 2, tabSize-timeSinceHoverRight, image.getHeight());
			for (int i = 0; i < 8; i++) {
				float f = i / 8f;
				g2d.setColor(new Color(
						(int) MathUtils.lerp(f, 38, 16),
						(int) MathUtils.lerp(f, 38, 16),
						(int) MathUtils.lerp(f, 38, 16)
				));
				g2d.fillRect(timeSinceHoverRight + i, frame.getHeight() / 2 - image.getHeight() / 2, 1, image.getHeight());
			}
			
			if (selectedBlockType!=propertyBlockType) {
				properties.clear();
				BlockRegistry.blocks.get(selectedBlockType).newInstance().addProperties(properties);
				propertyBlockType=selectedBlockType;
			}
			
			g2d.setTransform(source);
			g2d.translate(frame.getWidth()+(timeSinceHoverRight-tabSize),(frame.getHeight()/2)-(image.getHeight()/2)+3);
			int propertyX=frame.getWidth()+(timeSinceHoverRight-tabSize);
			int propertyY=(frame.getHeight()/2)-(image.getHeight()/2)+3;
			AtomicInteger propertyYOff= new AtomicInteger();
			boolean clicked=lMouseDown;
			properties.forEach(p->{
				p.draw(g2d);
				g2d.translate(0,34);
				if (clicked) {
					boolean hadFocus=p.hasFocus();
					p.click(mx-(propertyX+8),my-(propertyY+propertyYOff.get()+32));
					if (p.hasFocus()&&!hadFocus) {
						lMouseDown=false;
					}
				}
				propertyYOff.addAndGet(34);
			});
			g2d.setTransform(source);
		} catch (Throwable ignored) {}
		
		g2d.setColor(new Color(49, 51, 54).darker());
		g2d.fillRect(0,0,frame.getWidth(),buttonBarHeight);
		
		if (my<=buttonBarHeight+30) {
			allowPlacement=false;
		} else if (isHoveringElementPaneRight) {
			allowPlacement=false;
		} else {
			allowPlacement=true;
		}
		
		try {
			BufferedImage buttonUnpressed=ImageIO.read(buttonUnpressedFile);
			BufferedImage buttonPressed=ImageIO.read(buttonPressedFile);
			drawButton(g2d,128,buttonUnpressed,buttonPressed,"Grid",grid,-1);
			drawButton(g2d,192,buttonUnpressed,buttonPressed,"Collision",collisionOutlines,1);
			drawButton(g2d,256,buttonUnpressed,buttonPressed,"Save",true,-1);
		} catch (Throwable ignored) {}
		
//		g.setColor(new Color(0,255,0));
//		g.fillRect(mx-frame.getX()-16,my-frame.getY()-36,16,16);
	}
	
	private void drawButton(Graphics2D g2d,int x,BufferedImage buttonUnpressed,BufferedImage buttonPressed,String text,boolean value,int manualOffset) {
		if (my<=buttonBarHeight+30) {
			if (frame.getWidth()-x<mx&&mx<frame.getWidth()-x+64) {
				g2d.setColor(new Color(0x404040));
			} else {
				if (value) {
					g2d.setColor(new Color(0xFFFFFF));
				} else {
					g2d.setColor(new Color(0x939393));
				}
			}
		} else {
			if (value) {
				g2d.setColor(new Color(0xFFFFFF));
			} else {
				g2d.setColor(new Color(0x939393));
			}
		}
		g2d.drawImage(buttonUnpressed,frame.getWidth()-x,0,null);
		g2d.drawString(text,frame.getWidth()-x+32+manualOffset-getTextWidth(g2d.getFont(),text),20);
	}
	
	private static int getTextWidth(Font font,String s) {
		BufferedImage bimig=new BufferedImage(s.length()*3,15,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d=(Graphics2D)bimig.getGraphics();
		Color bg=new Color(255,255,255);
		g2d.setColor(bg);
		g2d.fillRect(0,0,bimig.getWidth(),bimig.getHeight());
		g2d.setColor(new Color(0));
		g2d.setFont(font);
		g2d.drawString(s,0,15);
		int maxX=0;
		for (int x=0;x<bimig.getWidth();x++) {
			if (
					checkPixel(true,bg,x,0,bimig)||
					checkPixel(true,bg,x,1,bimig)||
					checkPixel(true,bg,x,2,bimig)||
					checkPixel(true,bg,x,3,bimig)||
					checkPixel(true,bg,x,4,bimig)||
					checkPixel(true,bg,x,5,bimig)||
					checkPixel(true,bg,x,6,bimig)||
					checkPixel(true,bg,x,7,bimig)||
					checkPixel(true,bg,x,8,bimig)||
					checkPixel(true,bg,x,9,bimig)||
					checkPixel(true,bg,x,10,bimig)||
					checkPixel(true,bg,x,11,bimig)||
					checkPixel(true,bg,x,12,bimig)||
					checkPixel(true,bg,x,13,bimig)||
					checkPixel(true,bg,x,14,bimig)
			) {
				maxX=x;
			}
			if (x-maxX>=20) {
				return maxX;
			}
		}
		return maxX;
	}
	
	private static boolean checkPixel(boolean inverse,Color check,int x,int y,BufferedImage bimig) {
		return inverse==(bimig.getRGB(x,y)!=check.getRGB());
	}
	
	private static final int buttonBarHeight=32;
	
	private static boolean grid=true;
	private static boolean collisionOutlines=true;
	private static boolean lMouseDown=false;
	private static boolean rMouseDown=false;
	
	public static void main(String[] args) {
		frame.setSize(1000,1000);
		frame.add(instance);
		frame.setVisible(true);
		
		frame.setTitle(Game.title+"-Room Editor");
		
		commonSetup();
		
		frame.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
					 if (e.getKeyChar()=='w') wPressed=true;
				else if (e.getKeyChar()=='s') sPressed=true;
				else if (e.getKeyChar()=='a') aPressed=true;
				else if (e.getKeyChar()=='d') dPressed=true;
				else if (e.getKeyChar()=='r') rPressed=true;
				
				instance.properties.forEach(p->p.type(e.getKeyChar()));
			}
			@Override
			public void keyReleased(KeyEvent e) {
					 if (e.getKeyChar()=='w') wPressed=false;
				else if (e.getKeyChar()=='s') sPressed=false;
				else if (e.getKeyChar()=='a') aPressed=false;
				else if (e.getKeyChar()=='d') dPressed=false;
				else if (e.getKeyChar()=='r') rPressed=false;
			}
		});
		
		frame.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
					 if (e.getButton()==1) lMouseDown=true;
				else if (e.getButton()==3) rMouseDown=true;
			}
			@Override
			public void mouseReleased(MouseEvent e) {
					 if (e.getButton()==1) lMouseDown=false;
				else if (e.getButton()==3) rMouseDown=false;
			}
			@Override public void mouseEntered(MouseEvent e){}@Override public void mouseExited(MouseEvent e){}@Override public void mouseClicked(MouseEvent e){}
		});
		
		try {
			r=new Robot();
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
		
		while (frame.isDisplayable()&&frame.isVisible()) {
			instance.updateEditor();
			frame.repaint();
		}
		Runtime.getRuntime().exit(0);
	}
	
	private static Robot r;
	
	public void updateEditor() {
		int prevMX=mx;
		int prevMY=my;
		if (new Date().getTime()-Game.lastTick.getTime() > 100) {
			Game.lastTick=new Date();
			
			lastPlayerX=playerX;
			lastPlayerY=playerY;
			
			Point p=frame.getMousePosition();
			if (p!=null) {
				mx=p.x;
				my=p.y;
				
				Point p2=getEditorPos(mx,my);
				editorMX=p2.x;
				editorMY=p2.y;
			}
			
			if (dPressed) Game.playerX+=1;
			if (aPressed) Game.playerX-=1;
			if (sPressed) Game.playerY+=1;
			if (wPressed) Game.playerY-=1;
		}
		if (!isHoveringElementPane&&allowPlacement) {
			for (int dist=0;dist<128;dist++) {
				Point p2=getEditorPos(
						(int)MathUtils.lerp(dist/128f,prevMX,mx),
						(int)MathUtils.lerp(dist/128f,prevMY,my)
				);
				
				if (lMouseDown) {
					try {
						Class<? extends Block> blockClassPlace=BlockRegistry.blocks.get(selectedBlockType);
						Block placement=(Block)blockClassPlace.getMethod("newInstance", Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).invoke(blockClassPlace.newInstance(),p2.x,p2.y,10,10,new Color(255,255,255,255).getRGB());
						placement.readProperties(properties);
						template.addBlock(placement);
					} catch (Throwable ignored) {}
				}
				
				if (rMouseDown) template.removeBlock(p2.x,p2.y);
			}
		} else {
			AtomicInteger yPos=new AtomicInteger(0);
			AtomicInteger hover=new AtomicInteger(-1);
			BlockRegistry.blocks.forEach(blockClass->{
				int x=-64+timeSinceHover+15;
				int y=-15+(frame.getHeight()/2+(yPos.get()-2)*50);
				if (x<mx&&mx<x+45) {
					if (y<my&&my<y+40) {
						hover.set(yPos.get());
					}
				}
				yPos.getAndIncrement();
			});
			if (lMouseDown&&hover.get()!=-1) {
				selectedBlockType=hover.get();
				lMouseDown=false;
			}
			hoveredBlockType=hover.get();
			
			if (my<=buttonBarHeight+30) {
				if (frame.getWidth()-128<mx&&mx<frame.getWidth()-128+64) {
					if (lMouseDown) {
						grid=!grid;
						lMouseDown=false;
					}
				}
			}
			
			if (my<=buttonBarHeight+30) {
				if (frame.getWidth()-192<mx&&mx<frame.getWidth()-192+64) {
					if (lMouseDown) {
						collisionOutlines=!collisionOutlines;
						lMouseDown=false;
					}
				}
			}
			
			if (my<=buttonBarHeight+30) {
				if (frame.getWidth()-256<mx&&mx<frame.getWidth()-192+64) {
					if (lMouseDown) {
						File fi=new File(gameDir+"\\"+"rooms\\saved");
						fi.mkdirs();
						File fi2=new File(gameDir+"\\"+"rooms\\saved\\"+(new Date().getTime()+".room"));
						
						try {
							if (!fi2.exists()) {
								fi2.createNewFile();
							}
							
							FileWriter writer=new FileWriter(fi2);
							StringBuilder s= new StringBuilder();
							for (Block block:template.thisBlocks) {
								s.append(block.getClass().getName());
								s.append(",").append(block.x);
								s.append(",").append(block.y);
								s.append(",").append(block.width);
								s.append(",").append(block.height);
								s.append(",").append(block.color);
								s.append(block.getExtraInfo());
								s.append("\n");
							}
							writer.write(s.toString());
							writer.close();
						} catch (Throwable ignored) {}
						
						lMouseDown=false;
					}
				}
			}
		}
		try{Thread.sleep(1);}catch(Throwable ignored){}
	}
	
	int selectedBlockType=0;
	int propertyBlockType=-1;
	int hoveredBlockType=0;
	
	private Point getEditorPos(int mx,int my) {
		int x1=(((mx/4)+(int)playerX)-frame.getWidth()/8);
		int y1=(((my/4)+(int)playerY)-frame.getHeight()/8);
		
		int editorMX=((((mx/4)+(x1<0?-5:5)+(int)playerX)-frame.getWidth()/8)/10)*10;
		int editorMY=(((((my/4)+(y1<0?-13:-3)+(int)playerY)-frame.getHeight()/8)/10)*10);
		editorMY*=-1;
		
		return new Point(editorMX,editorMY);
	}
}
