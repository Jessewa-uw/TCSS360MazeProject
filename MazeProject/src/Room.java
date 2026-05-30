package MazeProject.src;

public class Room {

    private boolean myExit     = false;
    private boolean myEntrance = false;
    private int     myRow;
    private int     myCol;

    private final Door[] myDoors = new Door[4];

    public Room(int row, int col) {
        myRow = row;
        myCol = col;
    }

    public Door    getDoor(Direction dir)          { return myDoors[dir.ordinal()]; }
    public void    setDoor(Direction dir, Door d)  { myDoors[dir.ordinal()] = d;    }

    public void    setEntrance()  { myEntrance = true;   }
    public boolean isEntrance()   { return myEntrance;   }
    public void    setExit()      { myExit = true;       }
    public boolean isExit()       { return myExit;       }

    public int getRow() { return myRow; }
    public int getCol() { return myCol; }
}
