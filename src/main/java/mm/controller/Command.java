package mm.controller;

/**
 * Command interface for implementing undo/redo functionality.
 * Follows the Command pattern to encapsulate user actions.
 */
public interface Command {
    /**
     * Executes the command.
     */
    void execute();
    
    /**
     * Undoes the command.
     */
    void undo();
    
    /**
     * Returns a description of the command for debugging purposes.
     */
    String getDescription();
}
