package MazeProject.src.model;

public class Lockpick {
    public int myCount;
    public static int DEFAULT_COUNT = 15;

    public Lockpick() {
        this(DEFAULT_COUNT);
    }

    public Lockpick(int startingCount) {
        this.myCount = startingCount;
    }

    public boolean pick() {
        if (myCount <= 0) {
            return false;
        }
        myCount--;
        return myCount > 0;
    }

    public boolean hasRemaining() {
        return myCount > 0;
    }

    public int getCount(){
        return myCount;
    }

    public void add(int amount) {
        myCount += amount;
    }
}
