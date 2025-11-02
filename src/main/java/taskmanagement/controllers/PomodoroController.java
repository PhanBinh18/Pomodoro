package taskmanagement.controllers;

/*
Tạo 1 bộ đếm thời gian đếm ngược session
Xử lý sự kiện 2 nút start và stop task
Thêm 1 bộ phát nhạc bằng media player và nạp các bài hát vào 1 hộp chọn
Cảnh báo nếu thoát trước khi hooàn thành task
*/

import javafx.scene.media.AudioClip;
import taskmanagement.models.Music;
import taskmanagement.models.Task;
import taskmanagement.AppManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class PomodoroController implements Initializable {

    private static final String NOTIFICATION_SOUND_PATH = "/Notification/notification.mp3";
    private AudioClip notificationSound; // phát thông báo đã kết thúc session
    private Task task;
    private Timeline timeline;
    private Music music;
    private int songNumber; // Theo dõi bài hát hiện tại được chọn
    private MediaPlayer mediaPlayer;

    @FXML
    private Label countdownLabel, modeLabel, totalTimeLabel;
    @FXML
    private ComboBox<String> songPicker;
    @FXML
    private Button playPauseButton, exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.task = AppManager.selectedTask;
        if (this.task == null) {
            // Ghi log lỗi để dễ dàng debug
            System.err.println("Lỗi: PomodoroController được tải mà không có task nào được chọn.");

            // Hiển thị một thông báo cho người dùng và có thể đóng cửa sổ này
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể bắt đầu Pomodoro");
            alert.setContentText("Không có công việc nào được chọn. Vui lòng quay lại và chọn một công việc.");
            alert.showAndWait();

            // Không thực hiện các lệnh khởi tạo còn lại
            return;
        }
        countdownLabel.setText(formatTime((int) task.getFocusTime().toSeconds()));

        // Dùng 1 label hiển thị thời gian và update bằng TimeLine mỗi 1 giây để làm bộ đếm thời gian session
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> updateCountdown()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        notificationSound = new AudioClip(
                Objects.requireNonNull(getClass().getResource(NOTIFICATION_SOUND_PATH)).toString()
        );

        this.music = new Music();
        if (!music.getSongs().isEmpty()) {
            setUpListSong();
        }

        // Đặt xử lý sự kiện thoát cho nút thoát X
        AppManager.stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                handleExit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Cập nhật thời gian từng giây và nếu kết thúc session thì bật thông báo và chuyển session
    private void updateCountdown() {
        task.updateTime(Duration.seconds(1));
        if (task.isFinishedSession()) {
            notificationSound.play();
            if (task.isBreak()) {
                task.startFocus();
                modeLabel.setText("Focus");
            } else {
                task.startBreak();
                modeLabel.setText("Break");
            }
        }
        updateTimeLabel();
    }

    // Vào trạng thái focus và bật bộ đếm
    @FXML
    public void startPomodoroButton() {
        if (!task.isRunning()) {
            task.startFocus();
            updateTimeLabel();
            modeLabel.setText("Focus");
            timeline.play();
        }
    }

    // Vào trạng thái stop và tắt bộ đếm
    @FXML
    public void stopPomodoroButton() {
        if (task.isRunning()) {
            task.stop();
            timeline.stop();
            modeLabel.setText("Stop");
        }
    }

    private void updateTimeLabel() {
        countdownLabel.setText(formatTime((int) task.getSessionRemainingTime().toSeconds()));
        totalTimeLabel.setText("Focused time: " + formatTime((int) task.getTotalFocusTime().toSeconds()));
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    // Thoát cửa sổ pomodoro và thông báo xác nhận nếu chưa hoàn thành công việc
    @FXML
    private void handleExit() throws IOException {
        if (task.isDone()) {
            exitToDayWindow();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận thoát");
            int totalRemainTime = (int) task.getMandatoryTime().subtract(task.getTotalFocusTime()).toSeconds();
            int minutes = totalRemainTime / 60, seconds = totalRemainTime % 60;
            alert.setContentText(String.format("Còn %d phút %d giây nữa để hoàn thành" +
                    "\nTask sẽ tính là không hoàn thành nếu bạn thoát bây giờ", minutes, seconds));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                task.setTaskFailed();
                exitToDayWindow();
            }
        }
    }
    private void exitToDayWindow() throws IOException {
        if (mediaPlayer != null) mediaPlayer.stop();
        AppManager.switchToDayWindow();
        AppManager.stage.setOnCloseRequest(null);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void setUpListSong() {
        // Nạp danh sách bài hát vào combo box
        for (File song : music.getSongs()) {
            songPicker.getItems().add(
                    song.getName().substring(0, song.getName().lastIndexOf('.'))
            );
        }
        songPicker.getSelectionModel().selectFirst();
        loadSelectedSong();

        songPicker.getSelectionModel().selectedIndexProperty().addListener((_, _, newVal) -> {
            songNumber = newVal.intValue();
            loadSelectedSong();
            playPauseButton.setText("Play");
        });
    }

    private void loadSelectedSong() {
        // Việc chọn bài hát sẽ làm dừng bài hát hiện tại đang phát
        if (mediaPlayer != null && music.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            music.stopPlaying();
        }
        // Cập nhật bài bài hát hiện tại cho media phát
        File selectedSong = music.getSongs().get(songNumber);
        Media media = new Media(selectedSong.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    // Sử dụng 1 nút bấm để xử lý việc dừng và phát nhạc
    @FXML
    public void handlePlayPause() {
        if (music.getSongs().isEmpty()) return;

        if (music.isPlaying()) {
            mediaPlayer.pause();
            music.stopPlaying();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.play();
            music.startPlaying();
            playPauseButton.setText("Pause");
        }
    }
}
