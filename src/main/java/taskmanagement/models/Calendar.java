package taskmanagement.models;

// Calendar dùng để tải và lưu lại các week object

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // [MỚI] Cần import
import java.util.HashMap;
import java.util.List; // [MỚI] Cần import
import java.util.Map;

public class Calendar implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<LocalDate, Week> weeks = new HashMap<>();
    private LocalDate startOfCurrentWeek;

    // [MỚI] Danh sách để lưu trữ các công việc mặc định
    private List<Task> defaultTasks;

    // [MỚI] Tên tệp tin để lưu các công việc mặc định
    private static final String DEFAULT_TASKS_FILENAME = "default-tasks.dat";

    // Đặt startOfCurrentWeek là đầu tuần
    public Calendar() {
        loadDefaultTasks(); // [MỚI] Tải các task mặc định khi khởi động
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
            // [SỬA ĐỔI] Khi tạo Tuần mới, truyền danh sách defaultTasks vào
            return new Week(startOfCurrentWeek, defaultTasks);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Week) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // [SỬA ĐỔI] Nếu lỗi, cũng tạo Tuần mới với các task mặc định
            return new Week(startOfCurrentWeek, defaultTasks);
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

        // [MỚI] Gọi phương thức để lưu các task mặc định
        saveDefaultTasks();
    }

    private String getWeekFilePath(LocalDate weekStart) {
        return Paths.get(System.getProperty("user.home"),
                "Documents",
                "saved-weeks",
                weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".dat").toString();
    }

    // [MỚI] Lấy đường dẫn tệp lưu task mặc định
    private String getDefaultTasksFilePath() {
        return Paths.get(System.getProperty("user.home"),
                "Documents",
                "saved-weeks",
                DEFAULT_TASKS_FILENAME).toString();
    }

    // [MỚI] Tải danh sách task mặc định từ tệp
    @SuppressWarnings("unchecked") // Bỏ cảnh báo cast (List<Task>)
    private void loadDefaultTasks() {
        File file = new File(getDefaultTasksFilePath());
        if (!file.exists()) {
            this.defaultTasks = new ArrayList<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.defaultTasks = (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Không thể tải default tasks, tạo danh sách mới.");
            this.defaultTasks = new ArrayList<>();
        }
    }

    // [MỚI] Lưu danh sách task mặc định vào tệp
    private void saveDefaultTasks() throws IOException {
        Path directoryPath = Paths.get(System.getProperty("user.home"), "Documents", "saved-weeks");
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getDefaultTasksFilePath()))) {
            oos.writeObject(defaultTasks);
        }
    }

    // [MỚI] Các phương thức để Controller quản lý
    public void addDefaultTask(Task task) {
        this.defaultTasks.add(task);
    }

    public List<Task> getDefaultTasks() {
        return this.defaultTasks;
    }

    // (Bạn có thể thêm removeDefaultTask sau nếu cần)

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
