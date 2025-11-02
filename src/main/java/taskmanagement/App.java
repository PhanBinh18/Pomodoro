package taskmanagement;

import taskmanagement.models.Calendar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void init() {
        AppManager.calendar = new Calendar();
    }

    @Override
    public void start(Stage stage) throws IOException {
        AppManager.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/Fxml/main-window.fxml"));
        AppManager.mainWindow = new Scene(fxmlLoader.load());
        AppManager.switchToMainWindow();
    }

    @Override
    public void stop() throws IOException {
        AppManager.calendar.saveWeeksToFile();
    }

    public static void main(String[] args) {
        launch();
    }
}
