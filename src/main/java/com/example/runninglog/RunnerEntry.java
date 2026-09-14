package com.example.runninglog;

import javafx.beans.property.SimpleStringProperty;
import java.util.concurrent.TimeUnit;

public class RunnerEntry {

    private final SimpleStringProperty runnerName;
    private final SimpleStringProperty distance;
    private final SimpleStringProperty time;
    private final SimpleStringProperty pace;

    private final double distanceValue;
    private final long timeInSeconds;
    private final double paceInSeconds;

    public RunnerEntry(String runnerName, double distanceMiles, double timeMinutes) {
        this.runnerName = new SimpleStringProperty(runnerName);
        this.distanceValue = distanceMiles;
        this.distance = new SimpleStringProperty(String.format("%.2f mi", distanceMiles));

        this.timeInSeconds = Math.round(timeMinutes * 60.0);
        this.time = new SimpleStringProperty(formatTime(this.timeInSeconds));

        if (distanceMiles > 0) {
            this.paceInSeconds = (double) this.timeInSeconds / distanceMiles;
        } else {
            this.paceInSeconds = 0.0;
        }

        this.pace = new SimpleStringProperty(formatPace(this.paceInSeconds));
    }

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

    private String formatPace(double paceSeconds) {
        long totalPaceSeconds = Math.round(paceSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalPaceSeconds);
        long seconds = totalPaceSeconds - TimeUnit.MINUTES.toSeconds(minutes);

        return String.format("%d:%02d", minutes, seconds);
    }

    public String getRunnerName() { return runnerName.get(); }
    public String getDistance() { return distance.get(); }
    public String getTime() { return time.get(); }
    public String getPace() { return pace.get(); }


    public double getDistanceValue() { return distanceValue; }
    public long getTimeInSeconds() { return timeInSeconds; }
    public double getPaceInSeconds() { return paceInSeconds; }

    @Override
    public String toString() {
        return String.format("%s,%.2f,%s,%s",
                getRunnerName(),
                getDistanceValue(),
                getTime(),
                getPace());
    }
}