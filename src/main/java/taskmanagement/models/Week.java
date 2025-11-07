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

    // [SỬA ĐỔI] Khôi phục về constructor gốc, không nhận defaultTasks
    public Week(LocalDate startDate) {
        this.startDate = startDate;
        this.dayList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = startDate.plusDays(i);
            // [SỬA ĐỔI] Khôi phục về lời gọi Day() gốc
            dayList.add(new Day(dayDate));
        }
    }

    public List<Day> getDayList() {
        return dayList;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

}