package rocnikovkapc;

import lejos.robotics.navigation.Waypoint;

/**
 *
 * @author Petr
 */
public class WayGenerator {

    private final long TRACK_WIDTH;
    private final long STEP_LENGTH;
    private final long XRANGE;
    private final long YRANGE;

    public WayGenerator(long TRACK_WIDTH, long STEP_LENGTH, long XRANGE, long YRANGE) {
        this.TRACK_WIDTH = TRACK_WIDTH;
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
    }

    public Waypoint gnw(Waypoint actual) {
        boolean nahoru = Math.round(actual.x / (TRACK_WIDTH / 2)) == 1 || Math.round(actual.x / (TRACK_WIDTH / 2)) % 5 == 0 ? true : false;
        if (nahoru) {
            if (Math.abs(YRANGE - actual.y) < TRACK_WIDTH * 1.25) {
                return new Waypoint(Math.min(XRANGE - TRACK_WIDTH, actual.x + STEP_LENGTH), actual.y);
            } else {
                return new Waypoint(actual.x, Math.min(YRANGE - TRACK_WIDTH, actual.y + STEP_LENGTH));
            }
        } else {
            if (actual.y < TRACK_WIDTH * 1.25) {
                return new Waypoint(Math.min(XRANGE - TRACK_WIDTH, actual.x + STEP_LENGTH), actual.y);
            } else {
                return new Waypoint(actual.x, Math.max(TRACK_WIDTH, actual.y - STEP_LENGTH));
            }
        }
    }
}
