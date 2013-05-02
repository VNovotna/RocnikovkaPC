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
        if (Math.abs(previous.x - actual.x) < Math.abs(previous.y - actual.y)) { //pohyb po ose x/y
            //pohyb po y
            if (previous.y < actual.y) {//pohyb nahoru/dolu
                //nahoru
                long limit = YRANGE - Math.round(actual.x / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
                if (Math.abs(limit - actual.y) < 50) {//kontrola jestli neni na/blízko limitu
                    return right(actual);
                }
                return up(actual);
            } else {
                //pohyb dolu
                long limit = Math.round((XRANGE - actual.x) / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
                if (Math.abs(limit - actual.y) < 50) {//kontrola jestli neni na/blízko limitu
                    return left(actual);
                }
                return down(actual);
            }
        } else {
            //pohyb po ose x
            if (previous.x < actual.x) {//pohyb doprava/doleva
                //pohyb doprava
                long limit = XRANGE - Math.round((YRANGE - actual.y) / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
                if (Math.abs(limit - actual.x) < 50) {//kontrola jestli neni na/blízko limitu
                    return down(actual);
                }
                return right(actual);
            } else {
                //pohyb doleva
                long limit = STEP_LENGTH + Math.round(actual.y / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
                if (Math.abs(limit - actual.x) < 50) {//kontrola jestli neni na/blízko limitu
                    return up(actual);
                }
                return left(actual);
            }

        }
    }

//pohyb směrem dolu
    public Waypoint down(Waypoint actual) {
        long limit = Math.round((XRANGE - actual.x) / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
        long y = (long) Math.max(limit, actual.y - STEP_LENGTH);
        return new Waypoint(actual.x, y);
    }

//pohyb směrem nahoru
    public Waypoint up(Waypoint actual) {
        long limit = YRANGE - Math.round(actual.x / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
        long y = (long) Math.min(limit, actual.y + STEP_LENGTH);
        return new Waypoint(actual.x, y);
    }

//pohyb směrem doprava
    public Waypoint right(Waypoint actual) {
        long limit = XRANGE - Math.round((YRANGE - actual.y) / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
        long x = (long) Math.min(limit, actual.x + STEP_LENGTH);
        return new Waypoint(x, actual.y);
    }

//pohyb směrem doleva
    public Waypoint left(Waypoint actual) {
        long limit = STEP_LENGTH + Math.round(actual.y / (TRACK_WIDTH / 2)) * (TRACK_WIDTH / 2);
        long x = (long) Math.max(limit, actual.x - STEP_LENGTH);
        return new Waypoint(x, actual.y);
    }
}
