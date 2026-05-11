public class Room {

    // checks if room is exit room
    protected boolean myExit = false;

    // each room holds up to 4 doors
    private Door[] myDoors = new Door[4];

    private boolean myItem;

    private boolean doorsAvailable = false;

    // constructor
    public Room(Door[] doors) {
        myDoors = doors;
    }


}
