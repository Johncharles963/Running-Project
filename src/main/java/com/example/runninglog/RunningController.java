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

public class RunningController {

    @FXML private TextField nameInput;
    @FXML private TextField distanceInput;
    @FXML private TextField timeInput;
    @FXML private Button submitBtn;

    @FXML private TableView<RunnerEntry> runningTable;
    @FXML private TableColumn<RunnerEntry, String> runnerCol;
    @FXML private TableColumn<RunnerEntry, String> distanceCol;
    @FXML private TableColumn<RunnerEntry, String> timeCol;
    @FXML private TableColumn<RunnerEntry, String> paceCol;

    @FXML private Text sortLabel;

    @FXML private Circle distanceCircle;
    @FXML private Circle nameCircle;
    @FXML private Circle timeCircle;
    @FXML private Circle paceCircle;

    @FXML private ImageView distanceIcon;
    @FXML private ImageView nameIcon;
    @FXML private ImageView timeIcon;
    @FXML private ImageView paceIcon;

    @FXML private Button sortBtn;
    @FXML private Button csvBtn;

    private final ObservableList<RunnerEntry> data = FXCollections.observableArrayList();
    private SortType currentSortType = SortType.PACE;

    private enum SortType {
        NAME, DISTANCE, TIME, PACE
    }

    @FXML
    public void initialize() {
        runnerCol.setCellValueFactory(new PropertyValueFactory<>("runnerName"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        paceCol.setCellValueFactory(new PropertyValueFactory<>("pace"));

        runningTable.setItems(data);

        setupNumericField(distanceInput);
        setupNumericField(timeInput);

        updateSortIndicator(paceCircle, "Pace (Fastest First)");


        sortBtnClicked();
    }

    private void setupNumericField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleSubmit() {
        String name = nameInput.getText().trim();
        String distanceStr = distanceInput.getText().trim();
        String timeStr = timeInput.getText().trim();

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
            showAlert("Input Error", "Time must be a valid number in minutes.", Alert.AlertType.ERROR);
            return;
        }

        RunnerEntry newEntry = new RunnerEntry(name, distanceMiles, timeMinutes);
        data.add(newEntry);

        nameInput.clear();
        distanceInput.clear();
        timeInput.clear();
    }
    private void setSortType(SortType type, String labelText, Circle selectedCircle) {
        currentSortType = type;

        nameCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        distanceCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        timeCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");
        paceCircle.setStyle("-fx-fill: #d7ecff; -fx-stroke: #fbe3e3;");

        updateSortIndicator(selectedCircle, labelText);
    }

    private void updateSortIndicator(Circle circle, String labelText) {
        circle.setStyle("-fx-fill: #90ee90; -fx-stroke: #3cb371; -fx-stroke-width: 2px;");
        sortLabel.setText("Sort By: " + labelText);
    }


    @FXML
    private void nameIconClicked(MouseEvent event) {
        setSortType(SortType.NAME, "Runner's Name (A-Z)", nameCircle);
    }

    @FXML
    private void distanceIconClicked(MouseEvent event) {
        setSortType(SortType.DISTANCE, "Distance (Longest First)", distanceCircle);
    }

    @FXML
    private void timeIconClicked(MouseEvent event) {
        setSortType(SortType.TIME, "Time (Shortest First)", timeCircle);
    }

    @FXML
    private void paceIconClicked(MouseEvent event) {
        setSortType(SortType.PACE, "Pace (Fastest First)", paceCircle);
    }

    @FXML
    private void sortBtnClicked() {
        Comparator<RunnerEntry> comparator = null;

        switch (currentSortType) {
            case NAME:
                comparator = Comparator.comparing(RunnerEntry::getRunnerName);
                break;
            case DISTANCE:
                comparator = Comparator.comparing(RunnerEntry::getDistanceValue).reversed();
                break;
            case TIME:
                comparator = Comparator.comparing(RunnerEntry::getTimeInSeconds);
                break;
            case PACE:
                comparator = Comparator.comparing(RunnerEntry::getPaceInSeconds);
                break;
            default:
                return;
        }

        FXCollections.sort(data, comparator);
    }


    @FXML
    private void csvBtnClicked() {
        File file = new File("FinalProjectRunners.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Runner Name,Distance (Miles),Time,Pace (M:SS)\n");

            for (RunnerEntry entry : data) {
                writer.write(entry.toString() + "\n");
            }

            showAlert("Success", "Data saved successfully to:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("File Error", "Could not save file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}