package taskmanagement.controllers;

import taskmanagement.models.Day;
import taskmanagement.AppManager;
// [SỬA LỖI] Đảm bảo chúng ta import ĐÚNG Task Model (của bạn)
import taskmanagement.models.Task;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
// [SỬA LỖI] KHÔNG import javafx.concurrent.Task; ở đây nữa để tránh xung đột
// import javafx.concurrent.Task; // <-- XÓA DÒNG NÀY (nếu có)

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.time.LocalDate;
import java.time.LocalTime;

public class StatusUpdateService extends ScheduledService<Void> {

    // Đặt khoảng thời gian quét (ví dụ: 10 giây)
    private static final long CHECK_INTERVAL_SECONDS = 10;
    // Thời gian thông báo trước khi task bắt đầu (ví dụ: 5 phút)
    private static final long NOTIFY_BEFORE_MINUTES = 5;

    public StatusUpdateService() {
        setPeriod(Duration.seconds(CHECK_INTERVAL_SECONDS));
    }

    // [SỬA LỖI] Sử dụng tên đầy đủ "javafx.concurrent.Task"
    @Override
    protected javafx.concurrent.Task<Void> createTask() {
        // [SỬA LỖI] Sử dụng tên đầy đủ "javafx.concurrent.Task"
        return new javafx.concurrent.Task<>() {
            @Override
            protected Void call() {
                // Chúng ta cần cả hai logic:

                // 1. Logic cũ: Cập nhật trạng thái FAIL (dựa trên AppManager.selectedDay)
                updateFailedStatus(AppManager.selectedDay);

                // 2. Logic mới: Gửi thông báo (dựa trên ngày hôm nay)
                checkForNotifications();

                return null;
            }
        };
    }

    /**
     * Logic MỚI: Quét các task của NGÀY HÔM NAY để gửi thông báo.
     * Logic này chạy liên tục, bất kể người dùng đang xem ngày nào.
     */
    private void checkForNotifications() {
        if (AppManager.calendar == null) return;

        LocalDate todayDate = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Lấy ngày hôm nay từ Calendar (cần đảm bảo Calendar đã tải tuần này)
        AppManager.calendar.setToAnotherWeek(todayDate); // Đảm bảo tuần hiện tại được tải
        Day today = AppManager.calendar.getCurrentWeek().getDayList().stream()
                .filter(d -> d.getDate().isEqual(todayDate))
                .findFirst().orElse(null);

        if (today == null) return;

        // Quét các task của ngày hôm nay
        today.getTaskObservableList().stream()
                // Chỉ tìm task Sẵn sàng (READY) và Chưa được thông báo
                .filter(task -> task.isReady() && !task.hasBeenNotified()) // "Task" ở đây là taskmanagement.models.Task
                .forEach(task -> {
                    long secondsUntilStart = java.time.Duration.between(now, task.getStartTime()).getSeconds();

                    // Nếu thời gian bắt đầu nằm trong khoảng (0 giây) và (5 phút)
                    if (secondsUntilStart > 0 && secondsUntilStart <= (NOTIFY_BEFORE_MINUTES * 60)) {
                        // Đánh dấu là đã thông báo
                        task.setNotificationSent();

                        // Gửi thông báo pop-up (Phải chạy trên luồng UI)
                        Platform.runLater(() -> {
                            Notifications.create()
                                    .title("Công việc sắp bắt đầu!")
                                    .text(String.format("Công việc '%s' sẽ bắt đầu lúc %s.",
                                            task.getTaskName(), task.getStartTime().toString()))
                                    .position(Pos.BOTTOM_RIGHT)
                                    .hideAfter(Duration.seconds(10))
                                    .showInformation();
                        });
                    }
                });
    }

    /**
     * Logic CŨ: Cập nhật trạng thái FAIL cho các task đã quá hạn
     * (Chỉ chạy trên ngày mà người dùng đang chọn xem)
     */
    private void updateFailedStatus(Day selectedDay) {
        if (selectedDay == null) return; // Nếu không có ngày nào được chọn, bỏ qua

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        selectedDay.getTaskObservableList().stream()
                .filter(Task::isReady) // "Task" ở đây là taskmanagement.models.Task
                .forEach(task -> {
                    boolean isBeforeToday = selectedDay.getDate().isBefore(currentDate);
                    // Cho phép trễ 5 phút
                    boolean isTodayAndExpired = selectedDay.getDate().isEqual(currentDate)
                            && task.getStartTime().plusMinutes(5).isBefore(currentTime);

                    if (isBeforeToday || isTodayAndExpired) {
                        // Phải chạy trên luồng UI vì nó cập nhật ObservableList/Property
                        Platform.runLater(task::setTaskFailed);
                    }
                });
    }
}

