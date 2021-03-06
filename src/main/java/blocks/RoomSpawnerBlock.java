package blocks;

import block_properties.IBlockProperty;
import block_properties.IntegerProperty;
import block_properties.StringProperty;
import game.Game;
import rooms.Room;
import rooms.RoomTemplate;
import utils.MathUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class RoomSpawnerBlock extends Block implements IRoomRenderable, IBackgroundBlock, IEditorRenderable {
	public RoomSpawnerBlock(int x, int y, int width, int height, int color) {
		super(x, y, width, height, color);
	}
	
	public RoomSpawnerBlock(int x, int y, int width, int height, int color, boolean differentiator) {
		super(x, y, width, height, color, differentiator);
	}
	
	public RoomSpawnerBlock(){}
	
	private String roomTemplate="";
	private String roomPath="";
	private int offX;
	private int offY;
	
	private static int recursiveRenders=0;
	
	private RoomTemplate template=null;
	private Room room=null;
	
	private static boolean alwaysChooseFirst=false;
	
	@Override
	public void read(String s) {
		String[] info=s.split(",");
		File fi=new File(Game.gameDir+"\\rooms\\"+info[0].replace(",",""));
		if (!fi.exists()) {
			roomTemplate=info[0].replace(",","");
		} else {
			roomPath=info[0].replace(",","");
		}
		offX=Integer.parseInt(info[1].replace(",",""));
		offY=Integer.parseInt(info[2].replace(",",""));
	}
	
	private String getFile() {
		File fi=new File(Game.gameDir+"\\rooms\\"+roomPath);
		File fi2=fi.listFiles()[alwaysChooseFirst?0:new Random().nextInt(fi.listFiles().length)];
		String roomPath=shorten(fi2.getPath().replace(Game.gameDir+"\\rooms\\",""),".room".length());
		if (roomPath.equals("")) {
			return getFile();
		}
		return roomPath;
	}
	
	boolean isChecking=false;
	
	boolean alreadyFoundWrong=false;
	
	public static void reset() {
//		recursiveRenders=0;
	}
	
	private static String shorten(String s, int shave) {
		return s.substring(0,s.length()-shave);
	}
	
	@Override public void collide(long px,long py){}@Override public void draw(Graphics2D g){}@Override public boolean collidesPlayer(long px,long py){return false;}@Override public void drawBackground(Graphics2D g){this.drawInRoom(g);}
	
	private int attempts=0;
	
	@Override
	public void drawInRoom(Graphics2D g2d) {
		if (Game.startRender!=null) {
			if (Game.startRender.getTime()<new Date().getTime()+100) {
				recursiveRenders++;
				
				if (recursiveRenders<=0) {
					recursiveRenders=200;
				}
				
				if (recursiveRenders>30) {
					recursiveRenders=200;
				}
				
				//Get template
				if (template==null)
					if (!roomTemplate.equals(""))  template=(Game.loadOrGetRoom(roomTemplate));
					else if (!roomPath.equals("")) template=(Game.loadOrGetRoom(getFile()));
				
				//Create Room
				if (this.room==null) this.room=new Room(template,this.x-5+offX,(-this.y)-5+offY);
				
				//Render Room
				if (recursiveRenders<=5) {
					if (!alreadyFoundWrong&&Game.game.checkRoomValid(room)) {
						Game.queuedRooms.add(this.room);
						this.room.draw(g2d);
						g2d.setColor(new Color(0,0,0, MathUtils.limit(0,(int)((recursiveRenders/5f)*255),255)));
//						if (!alwaysChooseFirst) {
//							g2d.fillRect(room.getMinX(),room.getMinY(),room.getMaxX()-room.getMinX(),room.getMaxY()-room.getMinY());
//						}
					} else {
						for (int i=0;i<=2;i++) {
							//Get template
							if (template==null)
								if (!roomTemplate.equals(""))  template=(Game.loadOrGetRoom(roomTemplate));
								else if (!roomPath.equals("")) template=(Game.loadOrGetRoom(getFile()));
							
							//Create Room
							if (this.room==null) this.room=new Room(template,this.x-5+offX,(-this.y)-5+offY);
							
							//Check Valid
							if (Game.game.checkRoomValid(this.room)) {
								this.template=null;
								this.room=null;
							}
						}
						if (this.room==null) {
	//						template=null;
	//						room=null;
							alreadyFoundWrong=true;
							BlockBrick brick=new BlockBrick(x,y,width,height,0xFFFFFF,true);
							brick.draw(g2d);
						} else {
							recursiveRenders--;
							try {
								this.drawInRoom(g2d);
							} catch (Throwable err) {}
							recursiveRenders++;
						}
					}
				}
				recursiveRenders--;
			}
		}
	}
	
	@Override
	public void drawInEditor(Graphics2D g) {
		recursiveRenders=3;
		this.alwaysChooseFirst=true;
		Game.startRender=new Date(0);
//		this.drawInRoom(g);
	}
	
	@Override
	public void drawCollision(Graphics2D g) {
		g.setColor(new Color(0, 0, 255,128));
		g.drawRect(x,y,width,height);
	}
	
	@Override
	public Block newInstance(Integer x, Integer y, Integer width, Integer height, Integer color) {
		return new RoomSpawnerBlock(x+offX, y+offY, width, height, color);
	}
	
	@Override
	public Block copy() {
		RoomSpawnerBlock block=new RoomSpawnerBlock(x,y,width,height,color,true);
		block.roomTemplate=this.roomTemplate;
		block.roomPath=this.roomPath;
		if (this.template!=null) block.template=this.template;
		if (this.room!=null) block.room=this.room;
		block.offY=this.offY;
		block.offX=this.offX;
		block.alreadyFoundWrong=this.alreadyFoundWrong;
		return block;
	}
	
	@Override
	public void update() {
		recursiveRenders=0;
		
		attempts=0;
		
		//Get template
		if (template==null)
			if (!roomTemplate.equals(""))  template=(Game.loadOrGetRoom(roomTemplate));
			else if (!roomPath.equals("")) template=(Game.loadOrGetRoom(getFile()));
		
		//Create Room
		if (room==null) room=new Room(template,this.x-5+offX,this.y-5+offY);
		
		//Check that the player is in the room
		try {
			if (alreadyFoundWrong||room==null||template==null) {
				Game.blocksToRemove.add(this);
				Game.game.blocksToAdd.add(new BlockBrick(x,y,width,height,0xFFFFFF,true));
			} else if (room.containsPoint(Game.playerX,Game.playerY)) {
				//Add room to game
//				if (Game.game.checkRoomValid(room)) {
					Game.blocksToRemove.add(this);
					Game.game.rooms.add(room);
//				}
			}
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}
	
	@Override
	public void addProperties(ArrayList<IBlockProperty> properties) {
		super.addProperties(properties);
		properties.add(new StringProperty());
		properties.add(new IntegerProperty());
		properties.add(new IntegerProperty());
	}
	
	@Override
	public void readProperties(ArrayList<IBlockProperty> properties) {
		super.readProperties(properties);
		int i1=((IntegerProperty)properties.get(2)).getNumber();
		int i2=((IntegerProperty)properties.get(3)).getNumber();
		this.read(((StringProperty)properties.get(1)).text+","+i1+","+i2);
	}
	
	@Override
	public String getExtraInfo() {
		return ","+roomPath+","+offX+","+offY;
	}
}
