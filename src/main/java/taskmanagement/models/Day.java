package taskmanagement.models;

// Day object chứa các task và xử lý thêm hay xóa task

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.Serial;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Day implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    /* Dùng observable list để list view phần UI tự động cập nhật danh sách các task
    Observable list không lưu được nên để transient và tạo 1 list khác chỉ để lưu */
    private transient ObservableList<Task> taskObservableList;
    private List<Task> serializableList;

    public Day(LocalDate date) {
        this.date = date;
        this.taskObservableList = FXCollections.observableArrayList();
    }

    public LocalDate getDate() {
        return date;
    }

    public void addTask(Task task) {
        taskObservableList.add(task);
        sortTasksByTime();
    }

    private void sortTasksByTime() {
        taskObservableList.sort(Comparator.comparing(Task::getStartTime));
    }

    public void removeTask(Task task) {
        taskObservableList.remove(task);
    }

    public ObservableList<Task> getTaskObservableList() {
        return taskObservableList;
    }

    // Copy các task trong observable list để lưu lại
    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        serializableList = new ArrayList<>(taskObservableList);
        oos.defaultWriteObject();
    }

    // Tạo lại observable list
    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        taskObservableList = FXCollections.observableArrayList(serializableList);
    }

}