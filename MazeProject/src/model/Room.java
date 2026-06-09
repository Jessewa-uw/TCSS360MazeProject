package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single room in the maze grid
 * Adjacent rooms are connected through doors
 */
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // room is the exit
    protected boolean myExit = false;

    // room is the entrance
    protected boolean myEntrance = false;

    protected int myRow;

    protected int myCol;

    // each room holds up to 4 doors
    private final Door[] myDoors = new Door[4];

    private boolean myItem;

    /**
     * Constructs a room at given grid position with no doors
     * @param row the row index of the room
     * @param col the column index of the room
     */
    public Room(int row, int col) {
        myRow = row;
        myCol = col;
    }

    /**
     * Returns all doors attached to the room
     * @return a list of the rooms doors
     */
    public List<Door> getDoors() {
        List<Door> doors = new ArrayList<>();
        for (Door door : myDoors) {
            if (door != null) {
                doors.add(door);
            }
        }
        return doors;
    }

    /**
     * Returns door on corresponding wall
     * @param dir ordinal direction
     * @return the Door on that wall
     */
    public Door getDoor(int dir){
        return myDoors[dir];
    }

    /**
     * Marks room as maze entrance
     */
    public void setEntrance(){
        myEntrance = true;
    }

    /**
     * Returns true if room is the entrance to the maze
     * @return true if room is marked as entrance
     */
    public boolean isEntrance(){
        return myEntrance;
    }

    /**
     * Marks room as maze exit
     */
    public void setExit(){
        myExit = true;
    }

    /**
     * Returns true if this room is the exit
     * @return true if room is marked as entrance
     */
    public boolean isExit(){
        return myExit;
    }

    /**
     * Places a door on the wall corresponding to given direction
     * @param dir the ordinal direction
     * @param door door to place
     */
    public void setDoor(int dir, Door door){
        myDoors[dir] = door;
    }

    /**
     * Returns row index of this room in maze grid
     * @return row index
     */
    public int getRow() {
        return myRow;
    }

    /**
     * Returns column index of this room in the maze grid
     * @return column index
     */
    public int getCol() {
        return myCol;
    }




}
