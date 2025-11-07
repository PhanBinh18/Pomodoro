package taskmanagement.controllers;

/* Đặt 7 list view tương ứng với 7 ngày trong tuần
Tuần hiện tại sẽ được thay đổi bởi 2 nút next và previous hoặc chọn trong date picker
Khi tuần hiện tại thay đổi các task trong tuần được nạp lại vào các list view
Đặt sự kiện khi click vào list view nào sẽ chuyển sang cửa sổ ngày tương ứng
*/

// [MỚI] Thêm import cho Alert và Optional
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import taskmanagement.models.Calendar;
import taskmanagement.models.Day;
import taskmanagement.models.Task;
import taskmanagement.AppManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class CalendarWindowController implements Initializable {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ListView<Task> mondayListView, tuesdayListView, wednesdayListView, thursdayListView, fridayListView, saturdayListView, sundayListView;
    @FXML
    private Label mondayLabel, tuesdayLabel, wednesdayLabel, thursdayLabel, fridayLabel, saturdayLabel, sundayLabel;
    @FXML
    private Button nextButton, previousButton;
    @FXML
    private DatePicker datePicker;

    // [MỚI] Thêm FXML cho nút Xóa Hết
    @FXML
    private Button deleteAllButton;

    private Calendar calendar;
    private boolean isUpdatingDatePicker = false;

    private List<ListView<Task>> listViews;
    private List<Label> labels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        calendar = AppManager.calendar;
        datePicker.setValue(calendar.getStartOfCurrentWeek());

        listViews = List.of(mondayListView, tuesdayListView, wednesdayListView,
                thursdayListView, fridayListView, saturdayListView, sundayListView);
        labels = List.of(mondayLabel, tuesdayLabel, wednesdayLabel,
                thursdayLabel, fridayLabel, saturdayLabel, sundayLabel);

        setupListViewCellFactories();
        updateListViews();
        setupListViewWidths();
        // Lắng nghe khi nào thay đổi kích thước cửa sổ để tính toán lại kích thước list view
        rootPane.widthProperty().addListener((obs, oldWidth, newWidth) -> setupListViewWidths());
    }

    private void setupListViewCellFactories() {
        listViews.forEach(listView -> listView.setCellFactory(_ -> new TaskCellCalendarWindow()));
    }

    // [GIỮ LẠI] Giữ nguyên logic tô màu ngày hôm nay
    public void updateListViews() {
        List<Day> dayList = calendar.getCurrentWeek().getDayList();
        LocalDate today = LocalDate.now(); // Lấy ngày hôm nay

        IntStream.range(0, listViews.size()).forEach(i -> {
            // Gán task cho ListView (logic cũ)
            listViews.get(i).setItems(dayList.get(i).getTaskObservableList());

            // [MỚI] Cập nhật màu cho Label (Thứ)
            Day day = dayList.get(i);
            Label label = labels.get(i);

            if (day.getDate().isEqual(today)) {
                // Nếu là ngày hôm nay, tô màu xanh
                label.setStyle("-fx-background-color: #4CAF50; -fx-font-weight: bold;");
            } else {
                // Nếu không phải, trả về màu đỏ mặc định
                label.setStyle("-fx-background-color: #FF6666; -fx-font-weight: bold;");
            }
        });
    }

    /* Tính toán kích thước list view và label sao cho fit với chiều ngang của cửa sổ
    và cách 1 cạnh trên 1 khoảng cố định */
    private void setupListViewWidths() {
        double width = rootPane.getWidth() / listViews.size();

        IntStream.range(0, listViews.size()).forEach(i -> {
            double x = i * width;
            labels.get(i).setPrefWidth(width);
            labels.get(i).setLayoutX(x);
            labels.get(i).setLayoutY(80);

            listViews.get(i).setPrefWidth(width);
            listViews.get(i).setLayoutX(x);
            listViews.get(i).setLayoutY(110);
        });
    }

    // Chuyển sang cửa sổ ngày tương ứng khi list view được click
    @FXML
    public void handleDayClick(int dayIndex) throws IOException {
        AppManager.selectedDay = calendar.getCurrentWeek().getDayList().get(dayIndex);
        AppManager.switchToDayWindow();
        listViews.get(dayIndex).getSelectionModel().clearSelection();
    }
    @FXML
    public void mondayClicked() throws IOException { handleDayClick(0); }
    @FXML
    public void tuesdayClicked() throws IOException { handleDayClick(1); }
    @FXML
    public void wednesdayClicked() throws IOException { handleDayClick(2); }
    @FXML
    public void thursdayClicked() throws IOException { handleDayClick(3); }
    @FXML
    public void fridayClicked() throws IOException { handleDayClick(4); }
    @FXML
    public void saturdayClicked() throws IOException { handleDayClick(5); }
    @FXML
    public void sundayClicked() throws IOException { handleDayClick(6); }

    /* biến bool isUpdatingDatePicker theo dõi liệu date picker bị thay đổi
    do được pick trực tiếp hay do nút next và previous */
    @FXML
    private void handleChangeDate() {
        if (!isUpdatingDatePicker && datePicker.getValue() != null) {
            calendar.setToAnotherWeek(datePicker.getValue());
            updateListViews();
        }
    }
    @FXML
    private void handleNextButtonAction() {
        calendar.setToNextWeek();
        updateDatePicker();
        updateListViews();
    }
    @FXML
    private void handlePreviousButtonAction() {
        calendar.setToPreviousWeek();
        updateDatePicker();
        updateListViews();
    }
    private void updateDatePicker() {
        isUpdatingDatePicker = true;
        datePicker.setValue(calendar.getStartOfCurrentWeek());
        isUpdatingDatePicker = false;
    }

    // [MỚI] Thêm trình xử lý cho nút Xóa Hết Dữ Liệu
    @FXML
    private void handleDeleteAllTasks() {
        // Hiển thị hộp thoại xác nhận CỰC KỲ RÕ RÀNG
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận Xóa");
        alert.setHeaderText("BẠN CÓ CHẮC CHẮN KHÔNG?");
        alert.setContentText("Hành động này sẽ XÓA VĨNH VIỄN tất cả các công việc đã lưu.\n" +
                "Không thể hoàn tác. Bạn có chắc chắn muốn tiếp tục?");

        Optional<ButtonType> result = alert.showAndWait();

        // Chỉ tiếp tục nếu người dùng nhấn OK
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Gọi phương thức (sắp tạo) trong Model
                calendar.deleteAllData();

                // Làm mới giao diện (sẽ trống trơn)
                updateListViews();

                // Hiển thị thông báo thành công
                Alert doneAlert = new Alert(Alert.AlertType.INFORMATION);
                doneAlert.setTitle("Hoàn tất");
                doneAlert.setHeaderText("Đã xóa toàn bộ dữ liệu.");
                doneAlert.showAndWait();

            } catch (IOException e) {
                // Hiển thị lỗi nếu không xóa được file
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText("Không thể xóa dữ liệu.");
                errorAlert.setContentText("Đã xảy ra lỗi khi cố gắng xóa các tệp đã lưu: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
        // Nếu người dùng nhấn "Cancel", không làm gì cả.
    }
}