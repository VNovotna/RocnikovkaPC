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
    private int faze = 0;

    public ObstacleAvoider(long STEP_LENGTH, long XRANGE, long YRANGE) {
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
    }

    Waypoint avoidF1(Point obstacle, Pose originalPose) {
        float originalHeading = originalPose.getHeading();
        float originalX = originalPose.getX();
        float originalY = originalPose.getY();
        Pose newPose = new Pose();
        System.out.print("heading:  " + originalHeading);
        if (originalHeading > 2) {
            System.out.println(" -> nahoru");
            newPose.setLocation(originalX+STEP_LENGTH, originalY);
            newPose.setHeading(90);
        } else if (originalHeading < -2) {
            System.out.println(" -> dolu");
        } else { //rovne
            System.out.println(" -> rovne");
        }
        faze++;
        return new Waypoint(newPose);
    }

    Waypoint avoidF2(Point get, Pose robotPose) {
        return null;
        //pojede nahoru a vzdycky se podiva jestli se muze vratit
    }
    
    Waypoint Avoid(Pose originalPose){
        Point prekazka = TestingPanel.getLastFeature();
        
        return null;
    }
}
