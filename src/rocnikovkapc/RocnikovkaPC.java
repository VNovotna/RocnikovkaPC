package rocnikovkapc;

import java.io.IOException;
import java.io.InputStream;
import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;

/**
 *
 * @author viki
 */
public class RocnikovkaPC {

    public static void main(String[] args) throws InterruptedException {
        NXTConnector connector = new NXTConnector();
        boolean connected = connector.connectTo("btspp://");

        if (!connected) {
            System.err.println("Failed to connect to any NXT");
            System.exit(1);
        }
        InputStream dis = connector.getInputStream();

        for (int i = 1; i < 100; i++) {
            Delay.msDelay(20);

            try {
                //System.out.println("Recieving...");
                int recieved = dis.read();
                System.out.println("Received " + recieved);
                Thread.sleep(300);
            } catch (IOException ioe) {
                System.err.println("IO Exception reading bytes:");
                System.err.println(ioe.getMessage());
                break;
            }
        }

        try {
            dis.close();
            connector.close();
        } catch (IOException ioe) {
            System.out.println("IOException closing connection:");
            System.out.println(ioe.getMessage());
        }
    }
}
