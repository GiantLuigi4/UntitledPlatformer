package blocks;

import java.util.ArrayList;

public class BlockRegistry {
	public static ArrayList<Class<? extends Block>> blocks=new ArrayList<>();
	
	public static void registerBlocks() {
		blocks.add(BlockBrick.class);
		blocks.add(WaterBlock.class);
		blocks.add(WaterContainerBlock.class);
		blocks.add(RoomSpawnerBlock.class);
	}
}
