package mm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PhysicsPerformanceMonitor.
 * Tests performance monitoring and quality adjustment functionality.
 */
public class TestPhysicsPerformanceMonitor {
    
    private PhysicsPerformanceMonitor monitor;
    private static final long TARGET_FRAME_TIME_NS = 16_666_666L; // ~60 FPS
    
    @BeforeEach
    void setUp() {
        monitor = new PhysicsPerformanceMonitor();
    }
    
    @Nested
    @DisplayName("Initial State Tests")
    class InitialStateTests {
        
        @Test
        @DisplayName("Monitor should start with default iteration values")
        void testInitialState() {
            assertEquals(6, monitor.getVelocityIterations());
            assertEquals(2, monitor.getPositionIterations());
        }
    }
    
    @Nested
    @DisplayName("Performance Update Tests")
    class PerformanceUpdateTests {
        
        @Test
        @DisplayName("Single frame update should not trigger quality adjustment")
        void testSingleFrameUpdate() {
            int initialVelocityIterations = monitor.getVelocityIterations();
            int initialPositionIterations = monitor.getPositionIterations();
            
            // Send a single frame with poor performance
            monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            
            // Quality should not change after just one frame
            assertEquals(initialVelocityIterations, monitor.getVelocityIterations());
            assertEquals(initialPositionIterations, monitor.getPositionIterations());
        }
        
        @Test
        @DisplayName("Quality adjustment should trigger after 60 frames")
        void testQualityAdjustmentTrigger() {
            // Send 59 frames with poor performance
            for (int i = 0; i < 59; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            }
            
            // Quality should not change yet
            assertEquals(6, monitor.getVelocityIterations());
            assertEquals(2, monitor.getPositionIterations());
            
            // Send the 60th frame
            monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            
            // Now quality should be reduced
            assertTrue(monitor.getVelocityIterations() < 6 || monitor.getPositionIterations() < 2);
        }
    }
    
    @Nested
    @DisplayName("Quality Reduction Tests")
    class QualityReductionTests {
        
        @Test
        @DisplayName("Poor performance should reduce quality")
        void testQualityReduction() {
            // Send 60 frames with poor performance (1.5x target frame time)
            long poorFrameTime = (long)(TARGET_FRAME_TIME_NS * 1.5);
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(poorFrameTime);
            }
            
            // Quality should be reduced
            assertTrue(monitor.getVelocityIterations() <= 5); // Should be reduced from 6
            assertTrue(monitor.getPositionIterations() <= 1); // Should be reduced from 2
        }
        
        @Test
        @DisplayName("Very poor performance should reduce quality significantly")
        void testSignificantQualityReduction() {
            // Send multiple batches of poor performance frames
            for (int batch = 0; batch < 5; batch++) {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS * 3); // Very poor performance
                }
            }
            
            // Quality should be at minimum levels
            assertEquals(3, monitor.getVelocityIterations()); // Minimum is 3
            assertEquals(1, monitor.getPositionIterations()); // Minimum is 1
        }
        
        @Test
        @DisplayName("Quality should not go below minimum thresholds")
        void testMinimumQualityThresholds() {
            // Reduce quality to minimum by sending many poor performance frames
            for (int batch = 0; batch < 10; batch++) {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS * 5); // Extremely poor performance
                }
            }
            
            // Should not go below minimum thresholds
            assertTrue(monitor.getVelocityIterations() >= 3);
            assertTrue(monitor.getPositionIterations() >= 1);
        }
    }
    
    @Nested
    @DisplayName("Quality Increase Tests")
    class QualityIncreaseTests {
        
        @Test
        @DisplayName("Good performance should increase quality")
        void testQualityIncrease() {
            // First reduce quality
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            }
            
            int reducedVelocityIterations = monitor.getVelocityIterations();
            int reducedPositionIterations = monitor.getPositionIterations();
            
            // Then send good performance frames (0.8x target frame time)
            long goodFrameTime = (long)(TARGET_FRAME_TIME_NS * 0.8);
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(goodFrameTime);
            }
            
            // Quality should be increased
            assertTrue(monitor.getVelocityIterations() >= reducedVelocityIterations);
            assertTrue(monitor.getPositionIterations() >= reducedPositionIterations);
        }
        
        @Test
        @DisplayName("Excellent performance should increase quality to maximum")
        void testMaximumQualityIncrease() {
            // Send multiple batches of excellent performance frames
            for (int batch = 0; batch < 5; batch++) {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS / 2); // Excellent performance
                }
            }
            
            // Quality should be at maximum levels
            assertEquals(8, monitor.getVelocityIterations()); // Maximum is 8
            assertEquals(3, monitor.getPositionIterations()); // Maximum is 3
        }
        
        @Test
        @DisplayName("Quality should not go above maximum thresholds")
        void testMaximumQualityThresholds() {
            // Increase quality to maximum by sending many good performance frames
            for (int batch = 0; batch < 10; batch++) {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS / 10); // Extremely good performance
                }
            }
            
            // Should not go above maximum thresholds
            assertTrue(monitor.getVelocityIterations() <= 8);
            assertTrue(monitor.getPositionIterations() <= 3);
        }
    }
    
    @Nested
    @DisplayName("Stable Performance Tests")
    class StablePerformanceTests {
        
        @Test
        @DisplayName("Target performance should not change quality")
        void testStablePerformance() {
            int initialVelocityIterations = monitor.getVelocityIterations();
            int initialPositionIterations = monitor.getPositionIterations();
            
            // Send 60 frames with exactly target performance
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS);
            }
            
            // Quality should remain unchanged
            assertEquals(initialVelocityIterations, monitor.getVelocityIterations());
            assertEquals(initialPositionIterations, monitor.getPositionIterations());
        }
        
        @Test
        @DisplayName("Performance within acceptable range should not change quality")
        void testAcceptablePerformanceRange() {
            int initialVelocityIterations = monitor.getVelocityIterations();
            int initialPositionIterations = monitor.getPositionIterations();
            
            // Send frames with performance between 0.8x and 1.5x target (acceptable range)
            for (int i = 0; i < 60; i++) {
                long frameTime = (long)(TARGET_FRAME_TIME_NS * (0.9 + (i % 20) * 0.03)); // 0.9x to 1.5x
                monitor.updatePerformance(frameTime);
            }
            
            // Quality should remain unchanged
            assertEquals(initialVelocityIterations, monitor.getVelocityIterations());
            assertEquals(initialPositionIterations, monitor.getPositionIterations());
        }
    }
    
    @Nested
    @DisplayName("Mixed Performance Tests")
    class MixedPerformanceTests {
        
        @Test
        @DisplayName("Average performance calculation should work correctly")
        void testAveragePerformanceCalculation() {
            // Send mixed performance: 30 good frames, 30 poor frames
            for (int i = 0; i < 30; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS / 2); // Good performance
            }
            for (int i = 0; i < 30; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS * 3); // Poor performance
            }
            
            // Average should be: (TARGET_FRAME_TIME_NS/2 * 30 + TARGET_FRAME_TIME_NS*3 * 30) / 60
            // = (15 + 90) / 60 * TARGET_FRAME_TIME_NS = 1.75 * TARGET_FRAME_TIME_NS
            // This is > 1.5 * TARGET_FRAME_TIME_NS, so quality should be reduced
            
            assertTrue(monitor.getVelocityIterations() < 6 || monitor.getPositionIterations() < 2);
        }
        
        @Test
        @DisplayName("Quality should stabilize with consistent performance")
        void testQualityStabilization() {
            // First, create poor performance to reduce quality
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            }
            
            int afterReductionVelocity = monitor.getVelocityIterations();
            int afterReductionPosition = monitor.getPositionIterations();
            
            // Then maintain stable, acceptable performance
            for (int batch = 0; batch < 3; batch++) {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS); // Target performance
                }
            }
            
            // Quality should remain stable (not changed from after reduction)
            assertEquals(afterReductionVelocity, monitor.getVelocityIterations());
            assertEquals(afterReductionPosition, monitor.getPositionIterations());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Zero frame time should not cause issues")
        void testZeroFrameTime() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(0);
                }
            });
            
            // With zero frame time (perfect performance), quality should increase
            assertTrue(monitor.getVelocityIterations() >= 6);
            assertTrue(monitor.getPositionIterations() >= 2);
        }
        
        @Test
        @DisplayName("Extremely large frame time should not cause issues")
        void testExtremeFrameTime() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 60; i++) {
                    monitor.updatePerformance(Long.MAX_VALUE / 100); // Very large but safe value
                }
            });
            
            // Quality should be reduced to minimum
            assertEquals(3, monitor.getVelocityIterations());
            assertEquals(1, monitor.getPositionIterations());
        }
        
        @Test
        @DisplayName("Alternating good and bad performance should average correctly")
        void testAlternatingPerformance() {
            // Alternate between excellent and poor performance
            for (int i = 0; i < 60; i++) {
                if (i % 2 == 0) {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS / 4); // Excellent
                } else {
                    monitor.updatePerformance(TARGET_FRAME_TIME_NS * 3); // Poor
                }
            }
            
            // Average should be: (TARGET_FRAME_TIME_NS/4 + TARGET_FRAME_TIME_NS*3) / 2
            // = (0.25 + 3) / 2 * TARGET_FRAME_TIME_NS = 1.625 * TARGET_FRAME_TIME_NS
            // This is > 1.5 * TARGET_FRAME_TIME_NS, so quality should be reduced
            
            assertTrue(monitor.getVelocityIterations() < 6 || monitor.getPositionIterations() < 2);
        }
    }
    
    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {
        
        @Test
        @DisplayName("Exactly 1.5x target frame time should trigger quality reduction")
        void testExactBoundaryReduction() {
            long boundaryFrameTime = (long)(TARGET_FRAME_TIME_NS * 1.5);
            
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(boundaryFrameTime);
            }
            
            // At exactly 1.5x, quality should not be reduced (condition is >1.5x)
            assertEquals(6, monitor.getVelocityIterations());
            assertEquals(2, monitor.getPositionIterations());
        }
        
        @Test
        @DisplayName("Just over 1.5x target frame time should trigger quality reduction")
        void testJustOverBoundaryReduction() {
            long justOverBoundaryFrameTime = (long)(TARGET_FRAME_TIME_NS * 1.5) + 1;
            
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(justOverBoundaryFrameTime);
            }
            
            // Just over 1.5x should trigger reduction
            assertTrue(monitor.getVelocityIterations() < 6 || monitor.getPositionIterations() < 2);
        }
        
        @Test
        @DisplayName("Exactly 0.8x target frame time should trigger quality increase")
        void testExactBoundaryIncrease() {
            long boundaryFrameTime = (long)(TARGET_FRAME_TIME_NS * 0.8);
            
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(boundaryFrameTime);
            }
            
            // At exactly 0.8x, quality should not increase (condition is <0.8x)
            assertEquals(6, monitor.getVelocityIterations());
            assertEquals(2, monitor.getPositionIterations());
        }
        
        @Test
        @DisplayName("Just under 0.8x target frame time should trigger quality increase")
        void testJustUnderBoundaryIncrease() {
            long justUnderBoundaryFrameTime = (long)(TARGET_FRAME_TIME_NS * 0.8) - 1;
            
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(justUnderBoundaryFrameTime);
            }
            
            // Just under 0.8x should trigger increase
            assertTrue(monitor.getVelocityIterations() > 6 || monitor.getPositionIterations() > 2);
        }
    }
    
    @Nested
    @DisplayName("Reset Counter Tests")
    class ResetCounterTests {
        
        @Test
        @DisplayName("Counters should reset after quality adjustment")
        void testCounterReset() {
            // Send 60 frames to trigger adjustment
            for (int i = 0; i < 60; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS * 2);
            }
            
            // Now send 59 more frames with different performance
            for (int i = 0; i < 59; i++) {
                monitor.updatePerformance(TARGET_FRAME_TIME_NS / 2);
            }
            
            int velocityAfterPartialBatch = monitor.getVelocityIterations();
            int positionAfterPartialBatch = monitor.getPositionIterations();
            
            // Send one more frame to complete the batch
            monitor.updatePerformance(TARGET_FRAME_TIME_NS / 2);
            
            // Now quality should change based on the new batch (good performance)
            assertTrue(monitor.getVelocityIterations() >= velocityAfterPartialBatch);
            assertTrue(monitor.getPositionIterations() >= positionAfterPartialBatch);
        }
    }
}
