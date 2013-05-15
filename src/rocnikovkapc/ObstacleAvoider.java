package rocnikovkapc;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lejos.geom.Point;
import lejos.robotics.mapping.PCNavigationModel;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 *
 * @author viky
 */
public class ObstacleAvoider extends Thread{

    private final long STEP_LENGTH;
    private float originalX;
    private float originalY;
    private PCNavigationModel model;

    public ObstacleAvoider(long STEP_LENGTH, PCNavigationModel model) {
        this.STEP_LENGTH = STEP_LENGTH;
        this.model = model;
    }

    public Waypoint avoid(Pose originalPose) {
        float x = originalPose.getX();
        float y = originalPose.getY();
        System.out.print("Avoiding: " + x + "|" + y + "|" + originalPose.getHeading());
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
        System.out.println(" with wp: " + x + "|" + y + "|" + originalPose.getHeading());
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

    @Override
   public void run() {
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
           Waypoint lol = new Waypoint(x, y, repairHeading(p.getHeading() + 90));
                System.out.println("Jdu na: " + lol.x + "|" + lol.y);
                model.goTo(lol);
            } else {
                Waypoint lol = avoid(p);
                System.out.println("Jdu na: " + lol.x + "|" + lol.y);
                model.goTo(lol);
            }

            System.out.println("while("+Math.floor(model.getTarget().x) +"!="+ Math.floor(model.getRobotPose().getX()) +"||"+ Math.floor(model.getTarget().y) +"!="+ Math.floor(model.getRobotPose().getY())+")");
            //dokud robot neni tam kam ho poslal avoid()
            while (Math.floor(model.getTarget().x) != Math.floor(model.getRobotPose().getX()) || Math.floor(model.getTarget().y) != Math.floor(model.getRobotPose().getY())) {
                System.out.println("cyklusuju");
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ObstacleAvoider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("robot je tam kam ho poslal avoid()");

            last = current;
            current = getLastFeature();
            p = model.getRobotPose();
        } while (Math.abs(p.getX() - originalX) > STEP_LENGTH / 4 && Math.abs(p.getY() - originalY) > STEP_LENGTH / 4);

        TestingPanel.objizdeni = 2;
    }

    Point getLastFeature() {
        ArrayList<Point> features = model.getFeatures();
        Point feature = features.get(features.size() - 1);
        System.out.println("getLastFeature(): " + feature.x + "|" + feature.y);
        return feature;
    }
}
