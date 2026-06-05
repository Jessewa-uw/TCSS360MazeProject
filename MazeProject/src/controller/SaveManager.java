package controller;

import model.GameState;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;

/**
 * Single-slot persistence for a game in progress. Reads and writes one fixed
 * save file via Java serialization.
 */
public final class SaveManager {

    /** The one save slot. */
    private static final File SAVE_FILE = new File("savegame.dat");

    private SaveManager() {
    }

    /** @return true if a saved game exists. */
    public static boolean exists() {
        return SAVE_FILE.isFile();
    }

    /**
     * Writes the given game state to the save slot, overwriting any prior save.
     *
     * @param state the snapshot to persist
     * @throws IOException if writing fails
     */
    public static void save(GameState state) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        }
    }

    /**
     * Reads the saved game state from the save slot.
     *
     * @return the restored snapshot
     * @throws IOException            if reading fails
     * @throws ClassNotFoundException if the saved classes can't be resolved
     */
    public static GameState load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) in.readObject();
        }
    }
}
