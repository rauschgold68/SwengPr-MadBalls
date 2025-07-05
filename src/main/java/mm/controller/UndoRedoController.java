package mm.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages undo/redo operations using the Command pattern.
 * Maintains a history of commands and current position.
 */
public class UndoRedoController {
    private final List<Command> commands = new ArrayList<>();
    private int currentIndex = -1;
    private static final int MAX_HISTORY = 50; // Limit history to prevent memory issues
    
    /**
     * Executes a command and adds it to the history.
     * Clears any redo history when a new command is executed.
     */
    public void executeCommand(Command command) {
        command.execute();
        
        // Remove any commands after current index (clear redo history)
        if (currentIndex < commands.size() - 1) {
            commands.subList(currentIndex + 1, commands.size()).clear();
        }
        
        // Add new command
        commands.add(command);
        currentIndex++;
        
        // Limit history size
        if (commands.size() > MAX_HISTORY) {
            commands.remove(0);
            currentIndex--;
        }
    }
    
    /**
     * Undoes the last command if possible.
     * @return true if undo was performed, false if no command to undo
     */
    public boolean undo() {
        if (canUndo()) {
            commands.get(currentIndex).undo();
            currentIndex--;
            return true;
        }
        return false;
    }
    
    /**
     * Redoes the next command if possible.
     * @return true if redo was performed, false if no command to redo
     */
    public boolean redo() {
        if (canRedo()) {
            currentIndex++;
            commands.get(currentIndex).execute();
            return true;
        }
        return false;
    }
    
    /**
     * Checks if undo is possible.
     */
    public boolean canUndo() {
        return currentIndex >= 0;
    }
    
    /**
     * Checks if redo is possible.
     */
    public boolean canRedo() {
        return currentIndex < commands.size() - 1;
    }
    
    /**
     * Clears all command history.
     */
    public void clear() {
        commands.clear();
        currentIndex = -1;
    }
    
    /**
     * Gets the current command count for debugging.
     */
    public int getCommandCount() {
        return commands.size();
    }
}
