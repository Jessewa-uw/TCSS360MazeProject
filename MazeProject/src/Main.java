import controller.MazeController;
import model.Maze;
import view.MazeGUI2D;

public class Main {
    public static void main(String[] args) {
        Maze maze = new Maze();
        MazeGUI2D mazeGUI = new MazeGUI2D(maze);
    }
}
