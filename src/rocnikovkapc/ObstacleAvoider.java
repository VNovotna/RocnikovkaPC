package rocnikovkapc;

import java.util.ArrayList;
import lejos.geom.Point;
import lejos.robotics.mapping.PCNavigationModel;
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
    private float originalX;
    private float originalY;
    private int faze = 0;
    private final PCNavigationModel model;

    public ObstacleAvoider(long STEP_LENGTH, long XRANGE, long YRANGE, PCNavigationModel model) {
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
        this.model = model;
    }

    Waypoint avoidF1(Point obstacle, Pose originalPose) {
        float originalHeading = originalPose.getHeading();
        float originalX = originalPose.getX();
        float originalY = originalPose.getY();
        Pose newPose = new Pose();
        System.out.print("heading:  " + originalHeading);
        if (originalHeading > 2) {
            System.out.println(" -> nahoru");
            newPose.setLocation(originalX + STEP_LENGTH, originalY);
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

    public Waypoint avoid(Pose originalPose) {
        float x = originalPose.getX();
        float y = originalPose.getY();
        switch (Math.round(repairHeading(originalPose.getHeading()))) {
            case 90:
                x += STEP_LENGTH;
                break;
            case 180:
                y += STEP_LENGTH;
                break;
            case 0:
                y -= STEP_LENGTH;
                break;
            case -90:
                x -= STEP_LENGTH;
                break;
        }
        return new Waypoint(x, y, originalPose.getHeading());
    }

    float repairHeading(float heading) {
        heading = heading % 360;
        heading = Math.round(heading / 90) * 90;
        if (heading == 0) {
            heading = 180;
        } else if (heading == 180) {
            heading = 0;
        }
        return 180 - heading;
    }

    void bypass() {
        Pose p = model.getRobotPose();
        originalX = p.getX();
        originalY = p.getY();
        Point last;
        Point current = getLastFeature();
        model.goTo(avoid(p));
        do {
            last = current;
            current = getLastFeature();
            if (last == current) {
                float x = p.getX();
                float y = p.getY();
                switch (Math.round(repairHeading(p.getHeading()))) {
                    case 90:
                        x += STEP_LENGTH;
                        break;
                    case 180:
                        y += STEP_LENGTH;
                        break;
                    case 0:
                        y -= STEP_LENGTH;
                        break;
                    case -90:
                        x -= STEP_LENGTH;
                        break;
                }
                model.goTo(new Waypoint(x,y,repairHeading(p.getHeading()+90)));
            }else{
                model.goTo(avoid(p));
            }
            p = model.getRobotPose();
        } while (Math.abs(p.getX() - originalX) > 5 && Math.abs(p.getY() - originalY) > 5);
    }

    Point getLastFeature() {
        ArrayList<Point> features = model.getFeatures();
        return features.get(features.size() - 1);
    }
}
