package rocnikovkapc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import lejos.robotics.mapping.MapPanel;
import lejos.robotics.mapping.MenuAction;
import lejos.robotics.mapping.NavigationModel;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.mapping.NavigationPanel;

public class MovingPC extends NavigationPanel {

    private static final long serialVersionUID = 1L;
    // GUI Window size
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    private static final Dimension MAP_SIZE = new Dimension(700, 600);
    private static final Point INITIAL_VIEW_START = new Point(0, -10);
    private static final int INITIAL_ZOOM = 110;
    private static final String MAP_FILE = "floor.svg";
    private static final String FRAME_TITLE = "Path Test";
    private static int MESH_SPACE = 39;
    private static int CLEARANCE = 10;
    private JButton calculateButton = new JButton("Calculate path");
    private JButton followButton = new JButton("Follow Path");
    private JTextArea logArea = new JTextArea("ololol", 35, 35);
    private JScrollPane log = new JScrollPane(logArea);
    private JButton clearButton = new JButton("Clear log");

    public static void main(String[] args) throws Exception {
        MovingPC moving = new MovingPC();
        moving.run();
    }

    private MovingPC() {
        buildGUI();
    }

    /**
     * Build the GUI
     */
    @Override
    protected void buildGUI() {
        title = "Path Test";
        // Set the map size and suppress unwanted panels
        mapPaneSize = MAP_SIZE;
        showReadingsPanel = false;
        showLastMovePanel = false;
        showParticlePanel = false;
        showMoves = true;
        showZoomLabels = true;
        initialViewStart = INITIAL_VIEW_START;

        super.buildGUI();

        zoomSlider.setValue(INITIAL_ZOOM);

        // Set the map color
        mapPanel.colors[MapPanel.MAP_COLOR_INDEX] = Color.DARK_GRAY;

        // Add calculate and follow buttons
        commandPanel.add(calculateButton);
        commandPanel.add(followButton);

        // Display mesh
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

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                logArea.setText("");
            }
        });

        followButton.setEnabled(false);

        log.setBorder(BorderFactory.createTitledBorder("Log"));
        logPanel.add(log);
        logPanel.add(clearButton);
        logPanel.setPreferredSize(new Dimension(400, 650));
        add(logPanel);

        createMenu();
    }

    /**
     * Pop-up context menu
     */
    @Override
    protected void popupMenuItems(Point p, JPopupMenu menu) {
        // Include set pose and set target menu items
        menu.add(new MenuAction(NavigationModel.NavEvent.SET_POSE, "Set pose", p, model, this));
        menu.add(new MenuAction(NavigationModel.NavEvent.SET_TARGET, "Set target", p, model, this));
    }

    @Override
    public void eventReceived(NavEvent navEvent) {
        if (navEvent == NavEvent.PATH_COMPLETE) {
            // Add the path to the mesh again, when the path has been complete
            model.setPose(model.getRobotPose());
        }
    }

    @Override
    public void whenConnected() {
        connectButton.setEnabled(false);
        connectPanel.setBorder(BorderFactory.createTitledBorder("Connected"));
        model.setPose(model.getRobotPose());
    }

    @Override
    public void log(String message) {
        logArea.append(message + "\n");
    }

    public void run() throws Exception {
        model.setDebug(true);
        model.loadMap(MAP_FILE);
        model.setMeshParams(MESH_SPACE, CLEARANCE);

        openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, FRAME_TITLE, Color.white, menuBar);
    }
}