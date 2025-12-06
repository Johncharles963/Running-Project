package com.example.runninglog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

/**
 * Controller for the Running Log application.
 * Manages data input, pace calculation, table display, sorting, and CSV export.
 */
public class HelloController {

    // --- FXML UI Elements (Input) ---
    @FXML private TextField nameInput;
    @FXML private TextField distanceInput; // Distance in miles
    @FXML private TextField timeInput;     // Time in total minutes (e.g., 90.5)
    @FXML private Button submitBtn;

    // --- FXML UI Elements (Table) ---
    @FXML private TableView<RunnerEntry> runningTable;
    @FXML private TableColumn<RunnerEntry, String> runnerCol;
    @FXML private TableColumn<RunnerEntry, String> distanceCol;
    @FXML private TableColumn<RunnerEntry, String> timeCol;
    @FXML private TableColumn<RunnerEntry, String> paceCol;

    // --- FXML UI Elements (Sort & Save) ---
    @FXML private Text sortLabel;

    // FXML elements for the visual background indicators (Circles)
    @FXML private Circle distanceCircle;
    @FXML private Circle nameCircle;
    @FXML private Circle timeCircle;
    @FXML private Circle paceCircle;

    // FXML elements for the clickable icons (ImageViews)
    @FXML private ImageView distanceIcon;
    @FXML private ImageView nameIcon;
    @FXML private ImageView timeIcon;
    @FXML private ImageView paceIcon;

    @FXML private Button sortBtn;
    @FXML private Button csvBtn;

    // --- Data and State ---
    private final ObservableList<RunnerEntry> data = FXCollections.observableArrayList();
    private SortType currentSortType = SortType.PACE; // Default sort is by pace (fastest)

    // Enum to manage the current sorting preference
    private enum SortType {
        NAME, DISTANCE, TIME, PACE
    }

    /**
     * Called automatically after the FXML file has been loaded.
     * Initializes the TableView columns and sets up the data list.
     */
    @FXML
    public void initialize() {
        // 1. Initialize TableView columns to link to RunnerEntry properties
        runnerCol.setCellValueFactory(new PropertyValueFactory<>("runnerName"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        paceCol.setCellValueFactory(new PropertyValueFactory<>("pace"));

        runningTable.setItems(data);

        // 2. Add listeners to enforce numeric input where necessary (distance and time)
        setupNumericField(distanceInput);
        setupNumericField(timeInput); // APPLYING NUMERIC FILTER TO TIME INPUT

        // 3. Set default sort indicator (using the pace circle for the initial highlight)
        updateSortIndicator(paceCircle, "Pace (Fastest First)");

        // 4. Add some sample data for demonstration (Time is now in minutes)
        data.add(new RunnerEntry("Jane Doe", 3.106, 28.5));       // 5K in 28m 30s
        data.add(new RunnerEntry("John Smith", 13.109, 95.0));    // Half Marathon in 1h 35m 0s
        data.add(new RunnerEntry("Alice B.", 6.211, 45.0));       // 10K in 45m 0s

        // Apply initial sort
        sortBtnClicked();
    }

    /**
     * Helper to set up a TextField to accept only valid double values.
     */
    private void setupNumericField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allows optional digits, optional decimal point, and optional digits after the decimal.
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(oldValue);
            }
        });
    }

    // ------------------------------------------------------------------
    // 1 & 2. ADD ENTRY FUNCTIONALITY
    // ------------------------------------------------------------------

    /**
     * Handles the 'Add Entry' button click to process input and add a new entry.
     */
    @FXML
    private void handleSubmit() {
        String name = nameInput.getText().trim();
        String distanceStr = distanceInput.getText().trim();
        String timeStr = timeInput.getText().trim(); // Now minutes as a string

        // Input validation
        if (name.isEmpty() || distanceStr.isEmpty() || timeStr.isEmpty()) {
            showAlert("Input Error", "All fields must be filled out.", Alert.AlertType.ERROR);
            return;
        }

        double distanceMiles;
        try {
            distanceMiles = Double.parseDouble(distanceStr);
            if (distanceMiles <= 0) {
                showAlert("Input Error", "Distance must be greater than zero.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            // This should be caught by setupNumericField, but included for robustness
            showAlert("Input Error", "Distance must be a valid number.", Alert.AlertType.ERROR);
            return;
        }

        double timeMinutes;
        try {
            timeMinutes = Double.parseDouble(timeStr);
            if (timeMinutes <= 0) {
                showAlert("Input Error", "Time must be greater than zero minutes.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            // This should be caught by setupNumericField, but included for robustness
            showAlert("Input Error", "Time must be a valid number in minutes.", Alert.AlertType.ERROR);
            return;
        }

        // RunnerEntry handles the pace calculation internally
        RunnerEntry newEntry = new RunnerEntry(name, distanceMiles, timeMinutes);
        data.add(newEntry);

        // Clear inputs after successful submission
        nameInput.clear();
        distanceInput.clear();
        timeInput.clear();
    }

    // ------------------------------------------------------------------
    // 3. SORTING FUNCTIONALITY
    // ------------------------------------------------------------------

    /**
     * Sets the sort type based on the clicked icon and updates the visual indicator.
     * Takes the selected Circle element for styling.
     */
    private void setSortType(SortType type, String labelText, Circle selectedCircle) {
        currentSortType = type;

        // Reset all Circle background styles
        nameCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        distanceCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        timeCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        paceCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");

        updateSortIndicator(selectedCircle, labelText);
    }

    /**
     * Updates the visual indicator for the selected sort type.
     */
    private void updateSortIndicator(Circle circle, String labelText) {
        // Highlight green for the selected circle
        circle.setStyle("-fx-fill: #90ee90; -fx-stroke: #3cb371; -fx-stroke-width: 2px;");
        sortLabel.setText("Sort By: " + labelText);
    }

    /**
     * Handles click on the Name ImageView.
     */
    @FXML
    private void nameIconClicked(MouseEvent event) {
        setSortType(SortType.NAME, "Runner's Name (A-Z)", nameCircle);
    }

    /**
     * Handles click on the Distance ImageView.
     */
    @FXML
    private void distanceIconClicked(MouseEvent event) {
        setSortType(SortType.DISTANCE, "Distance (Longest First)", distanceCircle);
    }

    /**
     * Handles click on the Time ImageView.
     */
    @FXML
    private void timeIconClicked(MouseEvent event) {
        setSortType(SortType.TIME, "Time (Shortest First)", timeCircle);
    }

    /**
     * Handles click on the Pace ImageView.
     */
    @FXML
    private void paceIconClicked(MouseEvent event) {
        setSortType(SortType.PACE, "Pace (Fastest First)", paceCircle);
    }

    /**
     * Handles the 'Sort' button click to execute the currently selected sort type.
     */
    @FXML
    private void sortBtnClicked() {
        Comparator<RunnerEntry> comparator = null;

        switch (currentSortType) {
            case NAME:
                // a. Runner's name in A-Z alphabetical order
                comparator = Comparator.comparing(RunnerEntry::getRunnerName);
                break;
            case DISTANCE:
                // b. Distances in descending order (longest first)
                comparator = Comparator.comparing(RunnerEntry::getDistanceValue).reversed();
                break;
            case TIME:
                // c. Time in ascending order (shortest time first)
                comparator = Comparator.comparing(RunnerEntry::getTimeInSeconds);
                break;
            case PACE:
                // d. Pace in ascending order (fastest pace first - lowest seconds)
                comparator = Comparator.comparing(RunnerEntry::getPaceInSeconds);
                break;
            default:
                // Should not happen if initialized correctly
                return;
        }

        // Perform the sort on the ObservableList
        FXCollections.sort(data, comparator);
    }

    // ------------------------------------------------------------------
    // 4. CSV EXPORT FUNCTIONALITY
    // ------------------------------------------------------------------

    /**
     * Handles the 'Save to CSV' button click.
     */
    @FXML
    private void csvBtnClicked() {
        // Filename required by the project
        File file = new File("FinalProjectRunners.txt");
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV Header
            writer.write("Runner Name,Distance (Miles),Time,Pace (M:SS)\n");

            // Write data rows using the custom RunnerEntry.toString() method
            for (RunnerEntry entry : data) {
                writer.write(entry.toString() + "\n");
            }

            showAlert("Success", "Data saved successfully to:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("File Error", "Could not save file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------

    /**
     * Displays a JavaFX Alert dialog.
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}