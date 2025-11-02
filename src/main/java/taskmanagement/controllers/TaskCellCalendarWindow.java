package taskmanagement.controllers;

// Custom cell cho các list view trong cửa sổ chính

import taskmanagement.models.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class TaskCellCalendarWindow extends ListCell<Task> {
    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setGraphic(null);
        } else {
            Label taskNameLabel = new Label();
            HBox container;

            taskNameLabel.setWrapText(true);
            taskNameLabel.setText(task.getTaskName());
            taskNameLabel.setTextAlignment(TextAlignment.CENTER);
            taskNameLabel.setMaxWidth(getListView().getWidth() - 40);

            container = new HBox(taskNameLabel);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(5));

            // Đặt màu tương ứng với độ quan trong
            BackgroundFill backgroundFill = switch (task.getImportanceLevel()) {
                case LOW -> new BackgroundFill(Color.web("#A8E6CF"), new CornerRadii(5), Insets.EMPTY);
                case MEDIUM -> new BackgroundFill(Color.web("#FFD54F"), new CornerRadii(5), Insets.EMPTY);
                case HIGH -> new BackgroundFill(Color.web("#FFAB91"), new CornerRadii(5), Insets.EMPTY);
            };

            container.setBackground(new Background(backgroundFill));

            setGraphic(container);
        }
    }
}
