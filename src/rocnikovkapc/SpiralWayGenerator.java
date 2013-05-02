package rocnikovkapc;

import lejos.robotics.navigation.Waypoint;

/**
 *
 * @author Petr
 */
public class SpiralWayGenerator {

    private final long TRACK_WIDTH;
    private final long STEP_LENGTH;
    private final long XRANGE;
    private final long YRANGE;

    public SpiralWayGenerator(long TRACK_WIDTH, long STEP_LENGTH, long XRANGE, long YRANGE) {
        this.TRACK_WIDTH = TRACK_WIDTH;
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
    }

    public Waypoint generateNextWaypoint(Waypoint previous, Waypoint actual) {
        if (Math.abs(previous.x - actual.x) < Math.abs(previous.y - actual.y)) {
            if (previous.y < actual.y) {
                
            } else {
                
            }
        } else {
            if (previous.x < actual.x) {
                
            } else {
                
            }

        }

        return null;
    }
    public Waypoint down(Waypoint actual){
        
        return null;
    }
    public Waypoint up(Waypoint actual){
        
        return null;
    }
    public Waypoint right(Waypoint actual){
        
        return null;
    }
    public Waypoint left(Waypoint actual){
        
        return null;
    }
}
