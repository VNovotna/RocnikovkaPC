package nePrintVerze;

import java.util.logging.Level;
import java.util.logging.Logger;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 *
 * @author viky
 */
public class ObstacleAvoider extends Thread {

    private final long STEP_LENGTH;
    private float originalX;
    private float originalY;
    private boolean xPohlo = false;
    private boolean yPohlo = false;
    private TestingPanel panel;

    public ObstacleAvoider(long STEP_LENGTH, TestingPanel panel) {
        this.STEP_LENGTH = (long) ((long) STEP_LENGTH * 1.5);
        this.panel = panel;
    }
    //vygeneruje nový waypoint tak, aby robot popojel o krok na pravobok a směřoval stejným směrem kde viděl překážku
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
    //vypočítání vzdálenosti mezi dvěma body
    double distance(Point p, Point d) {
        return Math.sqrt(Math.pow(p.getX() - d.getX(), 2) + Math.pow(p.getY() - d.getY(), 2));
    }

    @Override
    public void run() {
        Pose p = panel.getModel().getRobotPose();
        originalX = p.getX();
        originalY = p.getY();
        Point last = null;
        Point current = getLastFeature();
        do {
            Waypoint nextWp;
            //robot má před sebou místo
            if (last == current || (last != null && distance(last, current) > STEP_LENGTH)) {
                float x = p.getX();
                float y = p.getY();
                switch ((int) Math.floor(repairHeading(p.getHeading()))) {
                    case 90:
                        y += STEP_LENGTH;
                        break;
                    case 180:
                        x -= STEP_LENGTH;
                        break;
                    case 0:
                        x += STEP_LENGTH;
                        break;
                    case -90:
                        y -= STEP_LENGTH;
                        break;
                }
                nextWp = new Waypoint(x, y, repairHeading(p.getHeading() + 90));
            //robot před sebou nemá volno
            } else {
                nextWp = avoid(p);
            }
            panel.getModel().goTo(nextWp);//neblokuje

            float cyklX = Math.abs(panel.getModel().getTarget().x - panel.getModel().getRobotPose().getX());
            float cyklY = Math.abs(panel.getModel().getTarget().y - panel.getModel().getRobotPose().getY());
            double cyklHeading = Math.abs(repairHeading(panel.getModel().getRobotPose().getHeading()) - repairHeading((float) panel.getModel().getTarget().getHeading()));
            
            //dokud robot neni tam kam ho poslal avoid() i se správným směrem kam kouká
            while (cyklX > 1 || cyklY > 1 || cyklHeading > 4) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ObstacleAvoider.class.getName()).log(Level.SEVERE, null, ex);
                }
                cyklX = Math.abs((panel.getModel().getTarget().x - panel.getModel().getRobotPose().getX()));
                cyklY = Math.abs((panel.getModel().getTarget().y - panel.getModel().getRobotPose().getY()));
                cyklHeading = Math.abs(repairHeading(panel.getModel().getRobotPose().getHeading()) - repairHeading((float) panel.getModel().getTarget().getHeading()));
            }
            try {
                Thread.sleep(1200);
            } catch (InterruptedException ex) {
                Logger.getLogger(ObstacleAvoider.class.getName()).log(Level.SEVERE, null, ex);
            }
            last = current;
            current = getLastFeature();
            p = panel.getModel().getRobotPose();
            System.out.println(Math.abs(p.getX() - originalX) + ">" + STEP_LENGTH / 4 + "||" + Math.abs(p.getY() - originalY) + ">" + STEP_LENGTH / 4);
        } while (bypassEnded(p));
        TestingPanel.objizdeni = 2;
    }

    Point getLastFeature() {
        return panel.getLastFeature();
    }
    //opraví heading na -90,0,90,180
    float repairHeading(float heading) {
        while (heading < 0) {
            heading += 360;
        }
        heading = heading % 360;
        heading = Math.round(heading / 90) * 90;
        heading = heading % 360;
        if (heading == 0) {
            heading = 180;
        } else if (heading == 180) {
            heading = 0;
        }
        return 180 - heading;
    }
    //kontroluje, zda se robot brátil na původní trasu za překážkou
    boolean bypassEnded(Pose p) {
        if (Math.abs(p.getX() - originalX) > STEP_LENGTH / 4) {
            xPohlo = true;
        }
        if (Math.abs(p.getY() - originalY) > STEP_LENGTH / 4) {
            yPohlo = true;
        }
        if (xPohlo && Math.abs(p.getX() - originalX) < STEP_LENGTH / 4) {
            return false;
        }
        if (yPohlo && Math.abs(p.getY() - originalY) < STEP_LENGTH / 4) {
            return false;
        }
        return true;
    }
}
