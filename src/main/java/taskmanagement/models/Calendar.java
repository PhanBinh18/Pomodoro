package taskmanagement.models;

// Calendar dùng để tải và lưu lại các week object

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// import java.util.ArrayList; // [ĐÃ XÓA] Không cần thiết nữa
import java.util.HashMap;
// import java.util.List; // [ĐÃ XÓA] Không cần thiết nữa
import java.util.Map;

public class Calendar implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<LocalDate, Week> weeks = new HashMap<>();
    private LocalDate startOfCurrentWeek;

    // [ĐÃ XÓA] Đã xóa List<Task> defaultTasks
    // [ĐÃ XÓA] Đã xóa DEFAULT_TASKS_FILENAME

    // Đặt startOfCurrentWeek là đầu tuần
    public Calendar() {
        // [ĐÃ XÓA] Đã xóa loadDefaultTasks();
        this.startOfCurrentWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        updateWeekMap();
    }

    // Them week vao map
    public void updateWeekMap() {
        if(!weeks.containsKey(startOfCurrentWeek)) {
            weeks.put(startOfCurrentWeek, loadWeek());
        }
    }

    private Week loadWeek() {
        File file = new File(getWeekFilePath(startOfCurrentWeek));
        if (!file.exists()) {
            // [SỬA ĐỔI] Khôi phục về phiên bản gốc
            return new Week(startOfCurrentWeek);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Week) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // [SỬA ĐỔI] Khôi phục về phiên bản gốc
            return new Week(startOfCurrentWeek);
        }
    }

    public void saveWeeksToFile() throws IOException {
        Path directoryPath = Paths.get(System.getProperty("user.home"), "Documents", "saved-weeks");
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Lưu các tuần như bình thường
        for (Map.Entry<LocalDate, Week> entry : weeks.entrySet()) {
            LocalDate weekStart = entry.getKey();
            Week week = entry.getValue();
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(getWeekFilePath(weekStart)))) {
                oos.writeObject(week);
            }
        }

        // [ĐÃ XÓA] Đã xóa saveDefaultTasks();
    }

    private String getWeekFilePath(LocalDate weekStart) {
        return Paths.get(System.getProperty("user.home"),
                "Documents",
                "saved-weeks",
                weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".dat").toString();
    }

    // [ĐÃ XÓA] Toàn bộ các phương thức liên quan đến Default Tasks đã bị xóa
    // (getDefaultTasksFilePath, loadDefaultTasks, saveDefaultTasks, addDefaultTask, getDefaultTasks)

    public void setToNextWeek() {
        startOfCurrentWeek = startOfCurrentWeek.plusWeeks(1);
        updateWeekMap();
    }

    public void setToPreviousWeek() {
        startOfCurrentWeek = startOfCurrentWeek.minusWeeks(1);
        updateWeekMap();
    }

    // Trả về đầu tuần của 1 date
    public void setToAnotherWeek(LocalDate date) {
        startOfCurrentWeek = date.with(DayOfWeek.MONDAY);
        updateWeekMap();
    }

    public LocalDate getStartOfCurrentWeek() {
        return startOfCurrentWeek;
    }

    public Week getCurrentWeek() {
        return weeks.get(startOfCurrentWeek);
    }
}