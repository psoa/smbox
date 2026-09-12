package br.com.psoa.smbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Shown when a second Smbox process is started so double-click users see a dialog
 * instead of a silent console message.
 */
public class AlreadyRunningApp extends Application {

    @Override
    public void start(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Smbox");
        alert.setHeaderText(null);
        alert.setContentText("Smbox is already running.");
        alert.showAndWait();
        Platform.exit();
    }
}
