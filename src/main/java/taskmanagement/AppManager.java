package taskmanagement;

/*
Class quản lý việc điều hướng giữa các scene và chứa các thuộc tính static
dựa trên nhu cầu có các biến global có thể truy cập từ mọi nơi trong mã nguồn
selectedDay và selectedTask là các đối tượng được quản lý bởi controller tương ứng
*/

// [SỬA] Đảm bảo import khớp với package (chữ thường)
import taskmanagement.controllers.StatusUpdateService;
import taskmanagement.models.Calendar;
import taskmanagement.models.Day;
import taskmanagement.models.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AppManager {
    public static Stage stage;
    public static Scene mainWindow;
    public static Calendar calendar;
    public static Day selectedDay;
    public static Task selectedTask;

    // Giữ nguyên instance
    private static final StatusUpdateService statusUpdateService = new StatusUpdateService();

    // [MỚI] Phương thức này khởi động service MỘT LẦN khi App.init() chạy
    public static void startStatusService() {
        if (statusUpdateService.getState() == Worker.State.READY) {
            statusUpdateService.start();
        } else if (statusUpdateService.getState() == Worker.State.CANCELLED) {
            statusUpdateService.reset();
            statusUpdateService.start();
        }
    }

    // [MỚI] Phương thức này tắt service MỘT LẦN khi App.stop() chạy
    public static void stopStatusService() {
        if (statusUpdateService.isRunning()) {
            statusUpdateService.cancel();
        }
    }

    // [ĐÃ SỬA] XÓA bỏ logic service ra khỏi
    public static void switchToDayWindow() throws IOException {
        // [ĐÃ XÓA] Toàn bộ logic `if (statusUpdateService...` đã bị xóa

        // [SỬA] Đổi /FXML/ thành /Fxml/ để khớp với App.java (trên Canvas)
        loadAndSetScene("/Fxml/day-window.fxml");
    }

    // [ĐÃ SỬA] XÓA bỏ logic service ra khỏi
    public static void switchToPomodoroWindow() throws IOException {
        // [ĐÃ XÓA] Logic `if (statusUpdateService...` đã bị xóa

        // [SỬA] Đổi /FXML/ thành /Fxml/ để khớp với App.java (trên Canvas)
        loadAndSetScene("/Fxml/pomodoro-window.fxml");
    }

    // [ĐÃ SỬA] XÓA bỏ logic service ra khỏi
    public static void switchToMainWindow() {
        // [ĐÃ XÓA] Logic `if (statusUpdateService...` đã bị xóa
        stage.setScene(mainWindow);
        stage.show();
    }

    private static void loadAndSetScene(String fxmlPath) throws IOException {
        // [SỬA] Sửa đường dẫn FXML (chữ hoa) để khớp với các file FXML của bạn
        FXMLLoader loader = new FXMLLoader(AppManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

