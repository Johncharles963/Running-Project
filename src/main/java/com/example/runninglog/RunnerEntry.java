package com.example.runninglog;

import javafx.beans.property.SimpleStringProperty;
import java.util.concurrent.TimeUnit;

/**
 * RunnerEntry represents a single race record, including calculation
 * of pace per mile. This class handles data formatting for display.
 */
public class RunnerEntry {

    // Properties for TableView display (String properties for JavaFX compatibility)
    private final SimpleStringProperty runnerName;
    private final SimpleStringProperty distance;
    private final SimpleStringProperty time;
    private final SimpleStringProperty pace;

    // Numerical values used for internal sorting/calculations
    private final double distanceValue; // Distance in miles (for sorting)
    private final long timeInSeconds;   // Total time in seconds (for sorting)
    private final double paceInSeconds; // Pace in seconds per mile (for sorting)

    /**
     * Constructor that parses time (in minutes) and calculates pace.
     * @param runnerName The name of the runner.
     * @param distanceMiles The distance run in miles.
     * @param timeMinutes The total time in minutes (as a double, e.g., 90.5).
     */
    public RunnerEntry(String runnerName, double distanceMiles, double timeMinutes) {
        this.runnerName = new SimpleStringProperty(runnerName);
        this.distanceValue = distanceMiles;
        this.distance = new SimpleStringProperty(String.format("%.2f mi", distanceMiles));

        // 1. Calculate time in seconds from total minutes (Input is now double)
        // We round the result to the nearest second for calculation consistency.
        this.timeInSeconds = Math.round(timeMinutes * 60.0);
        this.time = new SimpleStringProperty(formatTime(this.timeInSeconds));

        // 2. Calculate Pace (Added defensive check for division by zero)
        if (distanceMiles > 0) {
            this.paceInSeconds = (double) this.timeInSeconds / distanceMiles;
        } else {
            // Set pace to 0 for invalid distance, preventing division by zero (Infinity/NaN)
            this.paceInSeconds = 0.0;
        }

        // 3. Format Pace for display (M:SS format is typical for running pace)
        this.pace = new SimpleStringProperty(formatPace(this.paceInSeconds));
    }

    /**
     * Formats total seconds into H:MM:SS (or MM:SS if less than an hour) string.
     */
    private String formatTime(long totalSeconds) {
        long hours = TimeUnit.SECONDS.toHours(totalSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Formats pace (seconds/mile) into M:SS string.
     */
    private String formatPace(double paceSeconds) {
        long totalPaceSeconds = Math.round(paceSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalPaceSeconds);
        long seconds = totalPaceSeconds - TimeUnit.MINUTES.toSeconds(minutes);

        return String.format("%d:%02d", minutes, seconds);
    }

    // ------------------------------------------------------------------
    // Getters for TableView (Required by PropertyValueFactory)
    // ------------------------------------------------------------------

    public String getRunnerName() { return runnerName.get(); }
    public String getDistance() { return distance.get(); }
    public String getTime() { return time.get(); }
    public String getPace() { return pace.get(); }

    // ------------------------------------------------------------------
    // Getters for Sorting/Internal Logic
    // ------------------------------------------------------------------

    public double getDistanceValue() { return distanceValue; }
    public long getTimeInSeconds() { return timeInSeconds; }
    public double getPaceInSeconds() { return paceInSeconds; }

    // ------------------------------------------------------------------
    // CSV Export Format (matches the required order)
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        // Runner Name,Distance (Miles),Time,Pace (M:SS)
        return String.format("%s,%.2f,%s,%s",
                getRunnerName(),
                getDistanceValue(),
                getTime(),
                getPace());
    }
}