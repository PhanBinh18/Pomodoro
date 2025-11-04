package taskmanagement.controllers;

/* Đặt 7 list view tương ứng với 7 ngày trong tuần
Tuần hiện tại sẽ được thay đổi bởi 2 nút next và previous hoặc chọn trong date picker
Khi tuần hiện tại thay đổi các task trong tuần được nạp lại vào các list view
Đặt sự kiện khi click vào list view nào sẽ chuyển sang cửa sổ ngày tương ứng
*/

// [MỚI] Thêm các import cần thiết cho Dialog
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
// [MỚI] Thêm các import thời gian
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional; // [MỚI] Thêm import cho Optional
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

    // [MỚI] Thêm FXML cho nút mới
    @FXML
    private Button manageDefaultsButton;

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

    public void updateListViews() {
        List<Day> dayList = calendar.getCurrentWeek().getDayList();
        IntStream.range(0, listViews.size()).forEach(i ->
                listViews.get(i).setItems(dayList.get(i).getTaskObservableList()));
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

    // [MỚI] Thêm trình xử lý cho nút "Task Mặc định"
    @FXML
    private void handleManageDefaults() {
        // Hiển thị một Dialog để người dùng nhập thông tin task mặc định
        // Logic này tương tự như handleAddTask trong DayViewController
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Quản lý Task Mặc định");
        dialog.setHeaderText("Thêm một công việc mặc định mới");
        ButtonType addButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Tên công việc");

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Thời điểm bắt đầu (H:mm)");

        TextField focusTimeField = new TextField();
        focusTimeField.setPromptText("Quãng tập trung (phút)");

        TextField breakTimeField = new TextField();
        breakTimeField.setPromptText("Quãng nghỉ (phút)");

        ChoiceBox<Task.Priority> importanceLevelChoiceBox = new ChoiceBox<>();
        importanceLevelChoiceBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        importanceLevelChoiceBox.getSelectionModel().selectFirst();

        TextField mandatoryTimeField = new TextField();
        mandatoryTimeField.setPromptText("Thời gian bắt buộc (phút)");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Tên công việc:"), taskNameField,
                new Label("Thời gian bắt đầu:"), startTimeField,
                new Label("Thời gian tập trung:"), focusTimeField,
                new Label("Thời gian nghỉ:"), breakTimeField,
                new Label("Mức độ quan trọng:"), importanceLevelChoiceBox,
                new Label("Thời gian bắt buộc:"), mandatoryTimeField
        );

        dialog.getDialogPane().setContent(content);

        // Lọc sự kiện nút "Thêm" để xác thực
        final Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String taskName = taskNameField.getText();
                if (taskName == null || taskName.trim().isEmpty()) {
                    showErrorDialog("Dữ liệu không hợp lệ", "Tên công việc không được để trống.");
                    event.consume();
                    return;
                }

                LocalTime startTime = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("H:mm"));
                Duration focusTime = Duration.minutes(Integer.parseInt(focusTimeField.getText()));
                Duration breakTime = Duration.minutes(Integer.parseInt(breakTimeField.getText()));
                Task.Priority importanceLevel = importanceLevelChoiceBox.getValue();
                Duration mandatoryTime = Duration.minutes(Integer.parseInt(mandatoryTimeField.getText()));

                Task newTask = new Task(taskName, startTime, focusTime, breakTime, importanceLevel, mandatoryTime);

                // [MỚI] Logic kiểm tra xung đột trong tuần hiện tại
                if (isTimeConflictInCurrentWeek(newTask)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Xung đột thời gian trong tuần hiện tại!");
                    alert.setContentText("Task này bị trùng lịch với một task đã có trong tuần hiện tại. Bạn có chắc chắn muốn thêm không?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        event.consume(); // Ngăn dialog đóng và dừng lại
                        return;
                    }
                }

                // [QUAN TRỌNG] Thêm vào danh sách task mặc định của Calendar (cho tương lai)
                calendar.addDefaultTask(newTask);

                // [MỚI] Thêm (bản sao) vào tuần hiện tại
                List<Day> currentDays = calendar.getCurrentWeek().getDayList();
                for (Day day : currentDays) {
                    // Tạo một bản sao MỚI cho mỗi ngày
                    Task taskCopy = new Task(newTask.getTaskName(), newTask.getStartTime(),
                            newTask.getFocusTime(), newTask.getBreakTime(),
                            newTask.getImportanceLevel(), newTask.getMandatoryTime());
                    // Dùng phương thức addTask của Day để tự động sắp xếp
                    day.addTask(taskCopy);
                }

                // Thông báo thành công
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Thành công");
                // [MỚI] Cập nhật nội dung thông báo
                alert.setContentText("Đã thêm task mặc định. Task này đã được áp dụng cho tuần hiện tại và các tuần mới trong tương lai.");
                alert.showAndWait();

            } catch (DateTimeParseException e) {
                showErrorDialog("Lỗi định dạng", "Thời gian bắt đầu phải theo định dạng HH:mm.");
                event.consume(); // Ngăn dialog đóng lại
            } catch (NumberFormatException e) {
                showErrorDialog("Lỗi định dạng", "Thời gian tập trung, nghỉ và bắt buộc phải là số nguyên.");
                event.consume(); // Ngăn dialog đóng lại
            } catch (Exception e) {
                showErrorDialog("Dữ liệu không hợp lệ", "Vui lòng đảm bảo tất cả các trường đều được nhập đúng định dạng.");
                event.consume(); // Ngăn dialog đóng lại
            }
        });

        dialog.showAndWait();
    }

    // [MỚI] Thêm phương thức kiểm tra xung đột cho cả tuần hiện tại
    private boolean isTimeConflictInCurrentWeek(Task newTask) {
        List<Day> currentDays = calendar.getCurrentWeek().getDayList();
        LocalTime newStart = newTask.getStartTime();
        // Lấy thời gian kết thúc dựa trên mandatoryTime, giống logic của DayViewController
        LocalTime newEnd = newStart.plusSeconds((long) newTask.getMandatoryTime().toSeconds());

        for (Day day : currentDays) {
            // Tái sử dụng logic kiểm tra xung đột từ DayViewController
            boolean conflictInThisDay = day.getTaskObservableList().stream().anyMatch(existingTask -> {
                LocalTime existingStart = existingTask.getStartTime();
                LocalTime existingEnd = existingStart.plusSeconds((long) existingTask.getMandatoryTime().toSeconds());
                // Kiểm tra xem khoảng thời gian mới [newStart, newEnd] có chồng lấn với [existingStart, existingEnd] không
                return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            });

            if (conflictInThisDay) {
                return true; // Tìm thấy một xung đột vào ngày này
            }
        }
        return false; // Không tìm thấy xung đột nào trong cả tuần
    }

    // [MỚI] Thêm phương thức trợ giúp để hiển thị lỗi (tái sử dụng từ DayViewController)
    private void showErrorDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

