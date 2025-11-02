package taskmanagement.controllers;

// Chạy dịch vụ ngầm để đánh fail các task đã quá hạn theo thời gian thực

import taskmanagement.models.Day;
import taskmanagement.AppManager;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;


public class StatusUpdateService extends ScheduledService<Void> {
    public StatusUpdateService() {
        setPeriod(Duration.seconds(5));
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                updateStatus();
                return null;
            }
        };
    }
    private void updateStatus() {
        Day day = AppManager.selectedDay;
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        day.getTaskObservableList().stream().filter(taskmanagement.models.Task::isReady).forEach(task -> {
            boolean isBeforeToday = day.getDate().isBefore(currentDate);
            boolean isTodayAndExpired = day.getDate().isEqual(currentDate)
                    && task.getStartTime().plusMinutes(5).isBefore(currentTime);
            if (isBeforeToday || isTodayAndExpired) {
                Platform.runLater(task::setTaskFailed);
            }
        });
    }


}
