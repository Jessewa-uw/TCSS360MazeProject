package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    // constructor
    public Room(int row, int col) {
        myRow = row;
        myCol = col;
    }
    public List<Door> getDoors() {
        List<Door> doors = new ArrayList<>();
        for (Door door : myDoors) {
            if (door != null) {
                doors.add(door);
            }
        }
        return doors;
    }

    public Door getDoor(int dir){
        return myDoors[dir];
    }

    public void setEntrance(){
        myEntrance = true;
    }
    public boolean isEntrance(){
        return myEntrance;
    }
    public void setExit(){
        myExit = true;
    }
    public boolean isExit(){
        return myExit;
    }
    public void setDoor(int dir, Door door){
        myDoors[dir] = door;
    }

    public int getRow() {
        return myRow;
    }

    public int getCol() {
        return myCol;
    }




}
