package rocnikovkapc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lejos.robotics.mapping.MapPanel;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationPanel;
import lejos.robotics.mapping.SliderPanel;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.util.PilotProps;

/**
 *
 * @author viki
 */
public class TestingPanel extends NavigationPanel {

    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;
    private static final Dimension MAP_SIZE = new Dimension(700, 600);
    private static final Point INITIAL_VIEW_START = new Point(0, -10);
    private static final int INITIAL_ZOOM = 110;
//    private JButton calculateButton = new JButton("Calculate path");
//    private JButton followButton = new JButton("Follow Path");
    private JButton stopButton = new JButton("nigga halt!");
    private JTextArea logArea = new JTextArea("Tady běží logování \n", 35, 35);
    private JScrollPane log = new JScrollPane(logArea);
    private JButton clearButton = new JButton("Clear log");
    private SliderPanel setHeading, rotate, travelSpeed, rotateSpeed;
    private static WayGenerator wayGenerator;
    private static ObstacleAvoider obstacleAv;
    private static final long TRACK_WIDTH = 16; //sirka kol robota
    private static final long STEP_LENGTH = 16; //vlastne presnost mereni
    private static final long XRANGE = 64;
    private static final long YRANGE = 64;  //velikost mapovane oblasti
    private boolean objizdeni = false;

    public static void main(String[] args) throws IOException {
        PilotProps pp = new PilotProps();
        try {
            pp.loadPersistentValues();
        } catch (IOException ioe) {
            System.exit(1);
        }
        wayGenerator = new WayGenerator(TRACK_WIDTH, STEP_LENGTH, XRANGE, YRANGE);
        obstacleAv = new ObstacleAvoider(STEP_LENGTH, XRANGE, YRANGE);
        (new TestingPanel()).run();
    }

    private TestingPanel() {
        buildGUI();
    }

    @Override
    protected void buildGUI() {
        title = "GA NXT";
        // Set the map size and suppress unwanted panels
        mapPaneSize = MAP_SIZE;
        showReadingsPanel = false;
        showLastMovePanel = true;
        showParticlePanel = false;
        showConnectPanel = false;
        showStatusPanel = true;
        showMoves = true;
        showZoomLabels = true;
        initialViewStart = INITIAL_VIEW_START;

        super.buildGUI();

        zoomSlider.setValue(INITIAL_ZOOM);

        //Set the map color
        mapPanel.colors[MapPanel.MAP_COLOR_INDEX] = Color.DARK_GRAY;
        mapPanel.colors[MapPanel.ROBOT_COLOR_INDEX] = Color.PINK;
        //Display mesh
        showMesh = true;

//        calculateButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent event) {
//                model.calculatePath();
//                followButton.setEnabled(true);
//            }
//        });
//
//        followButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent event) {
//                model.followPath();
//            }
//        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                model.clearPath();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                logArea.setText("");
            }
        });

//        followButton.setEnabled(false);
//        //Add calculate and follow buttons 
//        commandPanel.add(calculateButton);
//        commandPanel.add(followButton);
        commandPanel.add(stopButton);

        //Display mesh
        showMesh = true;

//        followButton.setEnabled(false);

        log.setBorder(BorderFactory.createTitledBorder("Log"));
        logPanel.add(log);
        logPanel.add(clearButton);
        logPanel.setPreferredSize(new Dimension(400, 650));
        add(logPanel);

//        createMenu();
    }

    @Override
    public void log(String message) {
        logArea.append(message + "\n");
    }

    @Override
    protected void popupMenuItems(Point p, JPopupMenu menu) {
        // Include set pose and set target menu items
        menu.add(new MenuAction(NavigationModel.NavEvent.GOTO, "Go To", p, model, this));
        menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Place robot", p, model, this));
        menu.add(new MenuAction(NavigationModel.NavEvent.ADD_WAYPOINT, "Add Waypoint", p, model, this));
        menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target", p, model, this));
    }

    private void run() throws IOException {
        model.setDebug(true);
        model.connect("NXT");
        openInJFrame(this, WINDOW_WIDTH, WINDOW_HEIGHT, title, Color.LIGHT_GRAY, menuBar);
    }

    @Override
    public void eventReceived(NavigationModel.NavEvent navEvent) {
        super.eventReceived(navEvent);
        
        if (navEvent == NavigationModel.NavEvent.WAYPOINT_REACHED) {
            Waypoint nextWP = wayGenerator.gnw(new Waypoint(model.getRobotPose()));
            model.goTo(nextWP);
        }
        
        if (navEvent == NavigationModel.NavEvent.FEATURE_DETECTED) {
            ArrayList<lejos.geom.Point> features = model.getFeatures();
            for (lejos.geom.Point point : features) {
                float x = point.x;
                float y = point.y;
                System.out.println("prekazka: " + x + " | " + y);
                Pose pozice = model.getRobotPose();
                System.out.println("robot:    " + pozice.getX() + " | " + pozice.getY());

//                zjistit kdy jsem moc blízko a objet prekazku
                if (y > model.getRobotPose().getY() + TRACK_WIDTH || y < model.getRobotPose().getY() - TRACK_WIDTH || x > model.getRobotPose().getX() - TRACK_WIDTH) {
                    //System.out.println("Musim objet " + y + " >< " + model.getRobotPose().getY() + " + " + TRACK_WIDTH);
                    objizdeni = true;
                    obstacleAv.avoid(point, model.getRobotPose());
                }
                System.out.println("---");
            }
        }
    }

    @Override
    public void whenConnected() {
        super.whenConnected();
        model.setPose(new Pose(TRACK_WIDTH / 2, TRACK_WIDTH / 2, 90)); // proste tak musi zacinat 
        model.goTo(wayGenerator.gnw(new Waypoint(model.getRobotPose())));
    }
}
