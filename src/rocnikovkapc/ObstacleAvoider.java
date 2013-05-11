package rocnikovkapc;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 *
 * @author viky
 */
public class ObstacleAvoider {

    private final long STEP_LENGTH;
    private final long XRANGE;
    private final long YRANGE;

    public ObstacleAvoider(long STEP_LENGTH, long XRANGE, long YRANGE) {
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
    }

    Waypoint avoid(Point obstacle, Pose originalPose) {
        float heading = originalPose.getHeading();
        Pose newPose = new Pose();
        System.out.print("heading:  " + heading);
        if (heading > 2) {
            System.out.println(" -> nahoru");
        }
        else if (heading < -2) {
            System.out.println(" -> dolu");
        }
        else{ //rovne
            System.out.println(" -> rovne");
        }
        
        return new Waypoint(newPose);
    }
}
