package edu.brown.cs.azhang6.maps;

import java.io.IOException;

/**
 * Test run of {@link TrafficClient}.
 * 
 * @author aaronzhang
 */
public class TrafficClientTest {
    
    /**
     * Tests traffic client.
     * 
     * @param args unused
     * @throws IOException should not be thrown
     */
    public static void main(String[] args) throws IOException {
        // The server should be run on port 4568 before starting this program
        TrafficClient client = new TrafficClient("http://localhost:4568?last=");
        client.query();
        client.start(5000);
        while (true) {
            // This number should increase or stay the same
            System.out.println(client.getTraffic().size());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            
            }
        }
    }
}
