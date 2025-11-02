package taskmanagement.controllers;

/* Đặt 7 list view tương ứng với 7 ngày trong tuần
Tuần hiện tại sẽ được thay đổi bởi 2 nút next và previous hoặc chọn trong date picker
Khi tuần hiện tại thay đổi các task trong tuần được nạp lại vào các list view
Đặt sự kiện khi click vào list view nào sẽ chuyển sang cửa sổ ngày tương ứng
*/

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
}
