package taskmanagement;

// [MỚI] Sửa lại import cho models (chữ hoa)
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
        // [MỚI] Khởi động dịch vụ chạy ngầm ngay khi ứng dụng bắt đầu
        AppManager.startStatusService();
    }

    @Override
    public void start(Stage stage) throws IOException {
        AppManager.stage = stage;
        // [MỚI] Sửa đường dẫn FXML (chữ hoa) để khớp với các file FXML của bạn
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/Fxml/main-window.fxml"));
        AppManager.mainWindow = new Scene(fxmlLoader.load());
        AppManager.switchToMainWindow();
    }

    @Override
    public void stop() throws IOException {
        // [MỚI] Tắt dịch vụ chạy ngầm trước khi đóng ứng dụng
        AppManager.stopStatusService();
        // Lưu file sau khi đã tắt service
        AppManager.calendar.saveWeeksToFile();
    }

    public static void main(String[] args) {
        launch();
    }
}

