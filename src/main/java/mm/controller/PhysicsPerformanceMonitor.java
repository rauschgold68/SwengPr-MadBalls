package mm.controller;

/**
 * Monitors and adjusts physics performance settings for optimal simulation quality.
 * This class is responsible for tracking frame times and automatically adjusting
 * physics iteration counts to maintain smooth performance.
 */
public class PhysicsPerformanceMonitor {
    private int velocityIterations = 6;
    private int positionIterations = 2;
    private long frameTimeHistory = 0;
    private int frameCount = 0;
    private static final long TARGET_FRAME_TIME_NS = 16_666_666L; // ~60 FPS
    
    /**
     * Gets the current velocity iterations setting.
     * @return the number of velocity iterations
     */
    public int getVelocityIterations() {
        return velocityIterations;
    }
    
    /**
     * Gets the current position iterations setting.
     * @return the number of position iterations
     */
    public int getPositionIterations() {
        return positionIterations;
    }
    
    /**
     * Updates performance monitoring and adjusts quality settings if needed.
     * 
     * @param frameTimeNs the frame time in nanoseconds
     */
    public void updatePerformance(long frameTimeNs) {
        frameCount++;
        frameTimeHistory += frameTimeNs;
        
        // Adjust quality every 60 frames (about once per second at 60 FPS)
        if (frameCount >= 60) {
            long avgFrameTime = frameTimeHistory / frameCount;
            adjustQuality(avgFrameTime);
            resetCounters();
        }
    }
    
    /**
     * Adjusts physics quality based on performance.
     */
    private void adjustQuality(long avgFrameTime) {
        if (avgFrameTime > TARGET_FRAME_TIME_NS * 1.5) {
            reduceQuality();
        } else if (avgFrameTime < TARGET_FRAME_TIME_NS * 0.8) {
            increaseQuality();
        }
    }
    
    private void reduceQuality() {
        if (velocityIterations > 3) velocityIterations--;
        if (positionIterations > 1) positionIterations--;
    }
    
    private void increaseQuality() {
        if (velocityIterations < 8) velocityIterations++;
        if (positionIterations < 3) positionIterations++;
    }
    
    private void resetCounters() {
        frameCount = 0;
        frameTimeHistory = 0;
    }
}
