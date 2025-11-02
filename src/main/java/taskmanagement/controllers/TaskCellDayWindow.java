package taskmanagement.controllers;

// Custom cell cho list view trong cửa sổ ngày

import taskmanagement.models.Task;
import taskmanagement.AppManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.effect.ColorAdjust;

public class TaskCellDayWindow extends ListCell<Task> {
    @Override
    protected void updateItem(Task item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox cell = new HBox();
            cell.setSpacing(10);

            GridPane taskInfo = new GridPane();
            taskInfo.setVgap(5);
            taskInfo.setHgap(10);

            // Thêm thông tin vào grid pane info
            addTaskInfo(taskInfo, item.getTaskName(), 0, 0, FontWeight.BOLD);
            addTaskInfo(taskInfo, "Start time: " + item.getStartTime(), 0, 1, FontWeight.NORMAL);
            addTaskInfo(taskInfo, "Minimum duration: " + item.getMandatoryTime().toMinutes() + " min", 1, 1, FontWeight.NORMAL);
            addTaskInfo(taskInfo, "Focus interval: " + item.getFocusTime().toMinutes() + " min", 0, 2, FontWeight.NORMAL);
            addTaskInfo(taskInfo, "Break interval: " + item.getBreakTime().toMinutes() + " min", 1, 2, FontWeight.NORMAL);

            // Đặt màu dựa trên độ quan trọng
            BackgroundFill backgroundFill = switch (item.getImportanceLevel()) {
                case LOW -> new BackgroundFill(Color.web("#A8E6CF"), new CornerRadii(5), Insets.EMPTY);
                case MEDIUM -> new BackgroundFill(Color.web("#FFD54F"), new CornerRadii(5), Insets.EMPTY);
                case HIGH -> new BackgroundFill(Color.web("#FFAB91"), new CornerRadii(5), Insets.EMPTY);
            };
            cell.setBackground(new Background(backgroundFill));

            // Thay đổi màu của cell được select
            if (isSelected()) {
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(-0.2);
                cell.setEffect(colorAdjust);
                AppManager.selectedTask = item;
            } else {
                cell.setEffect(null);
            }

            Label statusLabel = new Label();
            statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            statusLabel.setBackground(new Background(new BackgroundFill(Color.web("#F0F0F0"), new CornerRadii(5), Insets.EMPTY)));
            statusLabel.setPadding(new Insets(5));
            statusLabel.setTextFill(Color.BLACK);
            // Gán status label với trạng thái của task
            StringBinding statusBinding = Bindings.createStringBinding(() -> {
                Task.State state = item.currentStateProperty().get();
                return switch (state) {
                    case DONE -> "DONE";
                    case FAIL -> "FAIL";
                    case READY -> "READY";
                    case FOCUS -> "FOCUS";
                    case BREAK -> "BREAK";
                    case STOPPED -> "STOPPED";
                };
            }, item.currentStateProperty());
            statusLabel.textProperty().bind(statusBinding);

            // Thêm khoảng không gian để phần status label luôn nằm bên phải
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            cell.getChildren().addAll(taskInfo, spacer, statusLabel);
            cell.setPrefHeight(80);
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setPadding(new Insets(10, 10, 10, 10));

            setGraphic(cell);
            setText(null);
        }
    }

    private void addTaskInfo(GridPane taskInfo, String text, int colIndex, int rowIndex, FontWeight fontWeight) {
        Text textNode = new Text(text);
        textNode.setFont(Font.font("System", fontWeight, 12));
        textNode.setFill(Color.BLACK);
        taskInfo.add(textNode, colIndex, rowIndex);
    }

}
