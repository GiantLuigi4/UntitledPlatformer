package blocks;

import rooms.Room;

public interface ISpecialValidator {
	boolean validate(Room room,Object cause);
}
