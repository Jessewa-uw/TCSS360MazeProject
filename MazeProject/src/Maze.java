import java.io.*;
import java.util.Scanner;


public class Maze {

    private Room myEntrance;
    private Room myExit;
    private Room[][] myMaze = new Room[3][3];
    private boolean myWon = false;
    private boolean myRunning = false;
    private int myRoomsLeft;
    // checks if there is still a path from current player location to exit
    public boolean checkPossible(){
        return true;
    }

    public void gameOver(){
        myRunning = false;
    }

    public void setRoomsLeft(int row, int col) {
        this.myRoomsLeft = row * col;
    }

    public Maze(Room[][] maze) {
        this.myMaze = maze;

    }

    public void setMyEntrance(int row, int col){
        myMaze[row][col] = myEntrance;
    }

    public void setMyExit(int row, int col){
        myMaze[row][col] = myExit;
    }



}
