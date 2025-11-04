package taskmanagement.models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Week implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Day> dayList;
    private final LocalDate startDate;

    // [SỬA ĐỔI] Hàm khởi tạo đã được cập nhật để nhận defaultTasks
    public Week(LocalDate startDate, List<Task> defaultTasks) {
        this.startDate = startDate;
        this.dayList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = startDate.plusDays(i);
            // [SỬA ĐỔI] Truyền danh sách task mặc định xuống cho mỗi Day
            dayList.add(new Day(dayDate, defaultTasks));
        }
    }

    public List<Day> getDayList() {
        return dayList;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

}
