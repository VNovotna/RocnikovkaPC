package rocnikovkapc;

/**
 *
 * @author Petr
 */
public class SpiralWayGenerator {

    private final double WHEEL_DIAMETER;
    private final double TRACK_WIDTH;
    private final long STEP_LENGTH;
    private final long XRANGE;
    private final long YRANGE;

    public SpiralWayGenerator(double WHEEL_DIAMETER, double TRACK_WIDTH, long STEP_LENGTH, long XRANGE, long YRANGE) {
        this.WHEEL_DIAMETER = WHEEL_DIAMETER;
        this.TRACK_WIDTH = TRACK_WIDTH;
        this.STEP_LENGTH = STEP_LENGTH;
        this.XRANGE = XRANGE;
        this.YRANGE = YRANGE;
    }
    
}
