package mm.model;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Service class responsible for handling collision detection and contact events.
 * This class manages win condition detection and collision event processing.
 */
public class ContactEventService {
    
    private final SimulationModel.PhysicsComponents physics;
    private final SimulationModel.SimulationState state;
    
    /**
     * Constructs a ContactEventService with access to simulation components.
     * 
     * @param physics the physics components containing the world
     * @param state the simulation state containing win listener
     */
    public ContactEventService(SimulationModel.PhysicsComponents physics, 
                              SimulationModel.SimulationState state) {
        this.physics = physics;
        this.state = state;
    }
    
    /**
     * Sets up a contact listener for the physics world to detect collisions.
     * This listener checks for win conditions, such as the ball reaching the win
     * platform or zone.
     */
    public void setupContactListener() {
        physics.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                handleContactBegin(contact);
            }

            @Override
            public void endContact(Contact contact) {
                // No action needed for end contact
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // No action needed for pre-solve
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                // No action needed for post-solve
            }
        });
    }

    /**
     * Handles the beginning of a contact between two physics bodies.
     * Extracts user data from both fixtures and checks for win conditions.
     * This method reduces nesting by early returns and delegates win condition
     * checking to a separate method.
     *
     * @param contact The contact event containing information about the colliding bodies
     */
    private void handleContactBegin(Contact contact) {
        Object userDataA = contact.getFixtureA().getBody().getUserData();
        Object userDataB = contact.getFixtureB().getBody().getUserData();

        // Early return if either body has no user data
        if (userDataA == null || userDataB == null) {
            return;
        }

        // Check for win condition and trigger if found
        if (isWinCondition(userDataA, userDataB)) {
            triggerWinCondition();
        }
    }

    /**
     * Determines if the contact between two objects represents a win condition.
     * A win condition occurs when a "winObject" comes into contact with either
     * a "winPlat" (win platform) or "winZone" (win zone). This method handles
     * both possible collision orders (A-B and B-A).
     *
     * @param userDataA The user data from the first colliding object
     * @param userDataB The user data from the second colliding object
     * @return true if this contact represents a win condition, false otherwise
     */
    private boolean isWinCondition(Object userDataA, Object userDataB) {
        return isWinObjectToTargetContact(userDataA, userDataB) || 
               isWinObjectToTargetContact(userDataB, userDataA);
    }

    /**
     * Checks if the first object is a win object and the second is a valid win target.
     * This helper method reduces code duplication by checking one direction of the
     * win condition (winObject touching winPlat or winZone).
     *
     * @param objectA The user data from the first object
     * @param objectB The user data from the second object
     * @return true if objectA is "winObject" and objectB is a win target
     */
    private boolean isWinObjectToTargetContact(Object objectA, Object objectB) {
        return "winObject".equals(objectA) && isWinTarget(objectB);
    }

    /**
     * Determines if an object is a valid win target.
     * Win targets are objects that, when touched by a win object, trigger
     * the win condition. Currently includes "winPlat" and "winZone".
     *
     * @param userData The user data from the object to check
     * @return true if the object is a valid win target
     */
    private boolean isWinTarget(Object userData) {
        return "winPlat".equals(userData) || "winZone".equals(userData);
    }

    /**
     * Triggers the win condition by stopping the simulation and notifying listeners.
     * This method handles all the actions that occur when a win condition is met:
     * logging the win, stopping the physics timer, setting the win screen visibility,
     * and notifying any registered win listeners.
     */
    private void triggerWinCondition() {
        System.out.println("WIN! ball1 reached the win condition!");
        
        // Defensive check - listener should always be set, but handle gracefully if not
        if (state.winListener == null) {
            System.err.println("Warning: Win condition triggered but no listener is registered");
            return;
        }

        // Stop the physics simulation
        physics.timer.stop();
        
        // Update UI state
        state.winScreenVisible = true;
        
        // Notify the listener
        state.winListener.onWin();
    }
}
