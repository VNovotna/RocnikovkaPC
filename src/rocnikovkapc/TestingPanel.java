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
    private JButton calculateButton = new JButton("Calculate path");
    private JButton followButton = new JButton("Follow Path");
    private JButton stopButton = new JButton("nigga halt!");
    private JTextArea logArea = new JTextArea("Tady běží logování \n", 35, 35);
    private JScrollPane log = new JScrollPane(logArea);
    private JButton clearButton = new JButton("Clear log");
    private SliderPanel setHeading, rotate, travelSpeed, rotateSpeed;
    private static WayGenerator wayGenerator;

    public static void main(String[] args) throws IOException {
        PilotProps pp = new PilotProps();
        try {
            pp.loadPersistentValues();
        } catch (IOException ioe) {
            System.exit(1);
        }
        wayGenerator = new WayGenerator(112, 100, 1000, 1000);
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

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                model.calculatePath();
                followButton.setEnabled(true);
            }
        });

        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                model.followPath();
            }
        });
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

        followButton.setEnabled(false);
        //Add calculate and follow buttons 
        commandPanel.add(calculateButton);
        commandPanel.add(followButton);
        commandPanel.add(stopButton);

        //Display mesh
        showMesh = true;

        followButton.setEnabled(false);

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
        System.out.println(navEvent.name());
        if (navEvent == NavigationModel.NavEvent.WAYPOINT_REACHED) {
            model.goTo(wayGenerator.gnw(new Waypoint(model.getRobotPose())));
        }
        if (navEvent == NavigationModel.NavEvent.FEATURE_DETECTED) {
            ArrayList<lejos.geom.Point> features = model.getFeatures();
            for (lejos.geom.Point point : features) {
                System.out.println(point.x + " | " + point.y);
            }
        }
    }

    @Override
    public void whenConnected() {
        super.whenConnected();
        model.goTo(wayGenerator.gnw(new Waypoint(model.getRobotPose())));
    }
}
