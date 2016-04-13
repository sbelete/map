package edu.brown.cs.azhang6.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Gets traffic data from server.
 * 
 * @author aaronzhang
 */
public class TrafficClient {
    
    /**
     * Map from way ID to traffic.
     */
    private final Map<String, Double> traffic = new HashMap<>();
    
    /**
     * URL of traffic server, without the timestamp.
     */
    private final String url;
    
    /**
     * Last time the server was queried.
     */
    private long lastTimestamp = 0;
    
    /**
     * Instantiates client that connects to given URL.  The URL does not include
     * the timestamp.
     * 
     * @param url url of traffic server without the timestamp
     */
    public TrafficClient(String url) {
        this.url = url;
    }
    
    /**
     * Queries traffic server.  Synchronized so other classes can choose to run
     * methods without worrying about the traffic changing in the middle of the
     * method.  For example, we can find the shortest path without worrying
     * about traffic changing in the middle of the algorithm.
     * 
     * @throws IOException if error reading from server
     */
    public synchronized void query() throws IOException {
        // Send get request
        URL fullURL = new URL(url + lastTimestamp);
        HttpURLConnection conn = (HttpURLConnection) fullURL.openConnection();
        conn.setRequestMethod("GET");
        // Get server response and use GSON to parse it
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            conn.getInputStream(), "UTF-8"))) {
            Object[][] response =
                Main.GSON.fromJson(reader.readLine(), Object[][].class);
            // For each way/traffic pair:
            for(Object[] wayTraffic : response) {
                String way = (String) wayTraffic[0];
                double trafficValue = (double) wayTraffic[1];
                traffic.put(way, trafficValue);
            }
        }
        lastTimestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }
    
    /**
     * Gets traffic for a given way.
     * 
     * @param wayId way ID
     * @return traffic
     */
    public double getTraffic(String wayId) {
        return traffic.getOrDefault(wayId, 1D);
    }
    
    /**
     * Gets all traffic.
     * 
     * @return traffic
     */
    public Map<String, Double> getTraffic() {
        return Collections.unmodifiableMap(traffic);
    }
    
    /**
     * Periodically queries traffic server after the specified number of
     * milliseconds.  Returns immediately.
     * 
     * @param interval number of milliseconds between queries
     */
    public void start(int interval) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    query();
                } catch (IOException e) {
                    // Stop querying traffic server if an error occurs
                    timer.cancel();
                }
            }}, interval, interval);
    }
}