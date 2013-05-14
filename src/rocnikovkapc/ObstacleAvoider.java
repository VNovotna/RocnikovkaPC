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

    private final long TRACK_WIDTH;
    private float originalX;
    private float originalY;
    private final PCNavigationModel model;

    public ObstacleAvoider(long STEP_LENGTH, PCNavigationModel model) {
        this.TRACK_WIDTH = STEP_LENGTH;
        this.model = model;
    }

    public Waypoint avoid(Pose originalPose) {
        float x = originalPose.getX();
        float y = originalPose.getY();
        switch (Math.round(repairHeading(originalPose.getHeading()))) {
            case 90:
                x += TRACK_WIDTH;
                break;
            case 180:
                y += TRACK_WIDTH;
                break;
            case 0:
                y -= TRACK_WIDTH;
                break;
            case -90:
                x -= TRACK_WIDTH;
                break;
        }
        return new Waypoint(x, y, originalPose.getHeading());
    }

    float repairHeading(float heading) {
        while (heading < 0) {
            heading += 360;
        }
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
        Point last = getLastFeature();
        Point current = getLastFeature();
        model.goTo(avoid(p));
        do {
            if (last == current) {
                float x = p.getX();
                float y = p.getY();
                switch (Math.round(repairHeading(p.getHeading()))) {
                    case 90:
                        x += TRACK_WIDTH;
                        break;
                    case 180:
                        y += TRACK_WIDTH;
                        break;
                    case 0:
                        y -= TRACK_WIDTH;
                        break;
                    case -90:
                        x -= TRACK_WIDTH;
                        break;
                }
                model.goTo(new Waypoint(x, y, repairHeading(p.getHeading() + 90)));
            } else {
                model.goTo(avoid(p));
            }

            p = model.getRobotPose();
            last = current;
            System.out.println("lastFeature: " + last.x + "|" + last.y);
            current = getLastFeature();
            System.out.println("lastFeature: " + current.x + "|" + current.y);
        } while (Math.abs(p.getX() - originalX) > TRACK_WIDTH / 4 && Math.abs(p.getY() - originalY) > TRACK_WIDTH / 4);

        TestingPanel.objizdeni = 2;
    }

    Point getLastFeature() {
        ArrayList<Point> features = model.getFeatures();
        Point feature = features.get(features.size() - 1);
        System.out.println("getLastFeature(): " + feature.x + "|" + feature.y);
        return feature;
    }
}
