package game;

import blocks.Block;
import blocks.BlockBrick;
import blocks.BlockRegistry;
import blocks.IBackgroundBlock;
import rooms.Room;
import rooms.RoomTemplate;
import utils.MathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Game extends JComponent {
	public static Game game=new Game();
	
	public static final String title="2D Game";
	
	public static JFrame frame=new JFrame(title);
	
	public static final String gameDir=System.getProperty("user.dir");
	
	public static final String player="haha_i_like_potatoes_lol_yes_haha_why_are_you_reading_this_now_haha.png";
	public static final String playerIdleTurn="haha_i_like_potatoes_and_im_staring_at_ur_soul_why_idk_haha.png";
	public static final File playerFile=new File(gameDir+"\\assets\\"+player);
	public static final File playerFileIdleTurn=new File(gameDir+"\\assets\\"+playerIdleTurn);
	
	public static final String brickOne="haha_brick_go__C_O_O_L__A_N_D__G_O_O_D.png";
	public static final String brickOneBorder="haha_brick_go__C_O_O_L__A_N_D__G_O_O_D_border.png";
	public static final File brickOneFile=new File(gameDir+"\\assets\\"+brickOne);
	public static final File brickOneFileBorder=new File(gameDir+"\\assets\\"+brickOneBorder);
	public static BufferedImage brickOneImage;
	public static BufferedImage brickOneImageBorder;
	
	public static final String water="water.png";
	public static final File waterFile=new File(gameDir+"\\assets\\"+water);
	public static BufferedImage waterImage;
	
	@Override
	public boolean isOptimizedDrawingEnabled() {
		return true;
	}
	
	public final ArrayList<Block> blocks=new ArrayList<>();
	public final ArrayList<Block> blocksToAdd=new ArrayList<>();
	
	public final ArrayList<Room> rooms=new ArrayList<>();
	
	public int direction=0;
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d=(Graphics2D)g;
		g2d.setColor(new Color(0));
		g2d.fillRect(0,0,frame.getWidth(),frame.getHeight());
		
		g2d.translate(frame.getWidth()/2,frame.getHeight()/2);
		g2d.scale(4,4);
		
		AffineTransform transform=g2d.getTransform();
		g2d.translate(
				-MathUtils.lerp(MathUtils.limit(0, (new Date().getTime()-lastTick.getTime())/100f, 1),playerX,lastPlayerX),
				-MathUtils.lerp(MathUtils.limit(0, (new Date().getTime()-lastTick.getTime())/100f, 1),playerY,lastPlayerY)
		);
		AffineTransform transform2=g2d.getTransform();
		try {
			rooms.forEach(r->r.draw(g2d));
			
			game.blocks.forEach(b->{
				if (b instanceof IBackgroundBlock)
					((IBackgroundBlock)b).drawBackground(g2d);
			});
			
			try {
				AffineTransform source=g2d.getTransform();
				g2d.setTransform(transform);
				if (direction==0) {
					Image img=ImageIO.read(playerFileIdleTurn);
					g2d.scale(0.5f,0.5f);
					g2d.drawImage(img,-8,-10,null);
				} else {
					Image img=ImageIO.read(playerFile);
					if  (direction==1) {
						g2d.scale(0.5f,0.5f);
					} else {
						g2d.scale(-0.5f,0.5f);
					}
					g2d.drawImage(img,-8,-10,null);
				}
				g2d.setTransform(source);
			} catch (Throwable err) {
				g2d.setColor(new Color(0x14FF00));
				g2d.fillRect(-5,-5,10,10);
			}
			
			game.blocks.forEach(b->b.draw(g2d));
			g2d.setTransform(transform);
			
			if (false) {
				g2d.translate(-playerX,-playerY);
				game.blocks.forEach((b)->b.drawCollision(g2d));
			}
		} catch (Throwable ignored) {}
		g2d.setTransform(transform2);
	}
	
	public boolean checkRoomValid(Room r) {
		for (Room room:this.rooms) {
			if (r.collidesWithRoom(room)) {
				return false;
			}
		}
		return true;
	}
	
	public static long playerX=0;
	public static long lastPlayerX=0;
	public static long playerY=0;
	public static long lastPlayerY=0;
	public static int playerYMot=0;
	public static int idleTime=0;
	
	public static boolean wPressed=false;
	public static boolean sPressed=false;
	public static boolean aPressed=false;
	public static boolean dPressed=false;
	public static boolean rPressed=false;
	public static boolean onGround=false;
	
	public static void commonSetup() {
		try {
			brickOneImage=ImageIO.read(brickOneFile);
		} catch (Throwable ignored) {}
		try {
			brickOneImageBorder=ImageIO.read(brickOneFileBorder);
		} catch (Throwable ignored) {}
		try {
			waterImage=ImageIO.read(waterFile);
		} catch (Throwable ignored) {}
		BlockRegistry.registerBlocks();
	}
	
	private static HashMap<String, RoomTemplate> templates=new HashMap<>();
	
	public static RoomTemplate loadOrGetRoom(String path) {
		if (!templates.containsKey(path)) {
			ArrayList<Block> blocks=new ArrayList<>();
			System.out.println("Loading Room:"+path);
			try {
				File fi=new File(gameDir+"\\rooms\\"+(path+".room"));
				System.out.println(fi.getPath()+" "+(fi.exists()?"exists":"does not exist"+"."));
				Scanner sc=new Scanner(new File(gameDir+"\\rooms\\"+(path+".room")));
				forEachLine(line->{
					try {
						String[] lineSplit=line.split(",",7);
						String part0=lineSplit[0].replace(",","");
						String part1=lineSplit[1].replace(",","");
						String part2=lineSplit[2].replace(",","");
						String part3=lineSplit[3].replace(",","");
						String part4=lineSplit[4].replace(",","");
						String part5=lineSplit[5].replace(",","");
						Class c=Class.forName(part0);
						Block placement=(Block)c.getMethod("newInstance", Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).invoke(c.newInstance(),Integer.parseInt(part1)+5,-Integer.parseInt(part2)+5,Integer.parseInt(part3),Integer.parseInt(part4),new Color(Integer.parseInt(part5)).getRGB());
						if (lineSplit.length>6) {
							if (lineSplit[6]!=null) {
								String part6=lineSplit[6];
								placement.read(part6);
							}
						}
						blocks.add(placement);
					} catch (Throwable err) {
//						err.printStackTrace();
					}
				},sc);
			} catch (Throwable err) {
//				err.printStackTrace();
			}
			System.out.println("Loaded room:"+path+", with "+blocks.size()+" blocks.");
			RoomTemplate template=new RoomTemplate(blocks);
			templates.put(path,template);
			return template;
		} else {
			return templates.get(path);
		}
	}
	
	private static void forEachLine(Consumer<String> function,Scanner sc) {
		while (sc.hasNextLine()) {
			function.accept(sc.nextLine());
		}
	}
	
	public static void main(String[] args) {
		frame.setSize(1000,1000);
		frame.add(game);
		frame.setVisible(true);
		
		commonSetup();
		
		frame.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar()=='w') {
					wPressed=true;
				}
				if (e.getKeyChar()=='s') {
					sPressed=true;
				}
				if (e.getKeyChar()=='a') {
					aPressed=true;
				}
				if (e.getKeyChar()=='d') {
					dPressed=true;
				}
				if (e.getKeyChar()=='r') {
					rPressed=true;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar()=='w') {
					wPressed=false;
				}
				if (e.getKeyChar()=='s') {
					sPressed=false;
				}
				if (e.getKeyChar()=='a') {
					aPressed=false;
				}
				if (e.getKeyChar()=='d') {
					dPressed=false;
				}
				if (e.getKeyChar()=='r') {
					rPressed=false;
				}
			}
		});

		RoomTemplate start=loadOrGetRoom("start");
		game.rooms.add(new Room(start,0,0));
		
		while (frame.isDisplayable()&&frame.isVisible()) {
			game.updateGame();
		}
		Runtime.getRuntime().exit(0);
	}
	
	public static Date lastTick=new Date();
	
	public void updateGame() {
		if (new Date().getTime()-lastTick.getTime() > 100) {
			onGround=false;
			
			lastTick=new Date();
			lastPlayerX=playerX;
			lastPlayerY=playerY;
			playerYMot+=1;
			playerYMot=MathUtils.limit(-1000,playerYMot,10);
			if (aPressed) {
				if (direction==1) {
					direction=0;
				} else {
					playerX-=1;
					direction=-1;
				}
			}
			if (dPressed) {
				if (direction==-1) {
					direction=0;
				} else {
					playerX+=1;
					direction=1;
				}
			}
			
			if (aPressed&&dPressed) {
				direction=0;
			}
			
			for (int i=0;i<Math.abs(playerYMot);i++) {
				playerY+=((playerYMot<0)?-1:1);
				game.blocks.forEach((b)->b.collide(playerX, playerY));
			}
			game.blocks.forEach((b)->b.collide(playerX, playerY));
			
			if (sPressed) {
				playerYMot=10;
			}
			if (wPressed&&onGround) {
				playerYMot=-10;
			}
			
			if (rPressed) {
				playerX=0;
				playerY=0;
				playerYMot=0;
			}
			
			if (!sPressed&&!wPressed&&!dPressed&&onGround&&!aPressed&&!rPressed) {
				idleTime++;
				if (idleTime>=30) {
					direction=0;
				}
			} else {
				idleTime=0;
			}
			
			rooms.forEach(r->{if(r.containsPoint(playerX,playerY))r.add(blocks);});
			
			blocks.forEach((b)->{if(!blocksToRemove.contains(b))b.update();});
			blocks.addAll(blocksToAdd);
			blocksToAdd.clear();
			blocksToRemove.forEach(i->blocks.remove(i));
			blocksToRemove.clear();
		}
		try {
			Thread.sleep(1);
		} catch (Throwable ignored) {}
		frame.repaint();
	}
	
	public static final ArrayList<Block> blocksToRemove=new ArrayList<>();
	
	public static Block getBlock(int x,int y) {
		AtomicReference<Block> block=new AtomicReference<>(null);
		game.blocks.forEach((b)->{
			if (b.collides(x,y)&&!blocksToRemove.contains(b)) block.set(b);
		});
		return block.get();
	}
	
	public static void removeBlock(int x,int y) {
		game.blocks.forEach((b)->{
			if (b.collides(x,y)) blocksToRemove.add(b);
		});
	}
}
