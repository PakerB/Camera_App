package source.demofx;

import javafx.animation.FadeTransition;
import javafx.application.Platform;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;


public class CameraStageController {

    @FXML
    private ImageView CameraFrame;

    @FXML
    private Button Go_Back_Button;

    @FXML
    private Button cameraButton;

    @FXML
    private CheckBox detection_checkbox;

    @FXML
    private Button filter_button;
    @FXML
    private AnchorPane anchorPane;
//    @FXML
//    private StackPane stackPane;

    protected AtomicBoolean isCameraActive = new AtomicBoolean(false);
    protected VideoCapture cameraCapture;
    String source = "C:\\MyOpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml";
    CascadeClassifier faceDetector = new CascadeClassifier(source);

    protected void startStopCamera() {
        if (isCameraActive.get()) {
            stopCamera();
            //lblnumber.setText("Person Number");
        } else {
//            if (isFilterValue.get()) {
//                startCameraWithFilter();
//            } else {
            startCamera();
            // }

        }
    }

    protected void startCamera() {
        cameraCapture = new VideoCapture(0);
        Mat frame = new Mat();
//        anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {
//            CameraFrame.setFitWidth(newVal.doubleValue());
//        });
//        anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
//            CameraFrame.setFitHeight(newVal.doubleValue());
//        });


        isCameraActive.set(true);

        new Thread(() -> {
            while (isCameraActive.get() && cameraCapture.read(frame)) {
                if (frame.empty()) {
                    System.out.println("No detection");
                    break;
                } else {
                    try {
                        if(detection_checkbox.isSelected()) {
                            Mat frame_gray = new Mat();
                            MatOfRect rostros = new MatOfRect();
                            Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);
                            Imgproc.equalizeHist(frame_gray, frame_gray);
                            double w = frame.width();
                            double h = frame.height();
                            faceDetector.detectMultiScale(frame_gray, rostros, 1.1, 2, 0 | CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
                            Rect[] facesArray = rostros.toArray();
                            //System.out.println("Số người có trong Camera: " + facesArray.length);

                            for (Rect face : facesArray) {
                                Point center = new Point((face.x + face.width * 0.5), (face.y + face.height * 0.5));
                                //Imgproc.ellipse(frame, center, new Size(face.width * 0.5, face.height * 0.5), 0, 0, 360, new Scalar(255, 0, 255), 4, 8, 0);
                                Imgproc.rectangle(frame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(123, 213, 23, 220), 2);
                                Imgproc.putText(frame, "This is person ", new Point(face.x, face.y - 20), 1, 1, new Scalar(255, 255, 255));
                            }
                        }
                        Platform.runLater(() -> {
                            MatOfByte mem = new MatOfByte();
                            Imgcodecs.imencode(".bmp", frame, mem);
                            InputStream in = new ByteArrayInputStream(mem.toArray());
                            Image im = new Image(in);
                            CameraFrame.setImage(im);
                        });

                        Thread.sleep(50);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void stopCamera() {
        isCameraActive.set(false);
        if (cameraCapture != null) {
            cameraCapture.release(); // Giải phóng tài nguyên camera
        }
        Platform.runLater(() -> CameraFrame.setImage(null));
    }
    @FXML
    public void clickCapture(ActionEvent event) {
        Image capturedImage = captureImage();
        //createFlashEffect();

        if (capturedImage == null) {
            showAlert("No Image", "Failed to capture image.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Save Captured Image");
        dialog.setHeaderText("Enter a name for the captured image:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Image Name");

        grid.add(new Label("Name: "), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return nameField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert("Invalid Name", "Image name cannot be empty.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.setInitialFileName(name + ".png");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                try {
                    BufferedImage bImage = SwingFXUtils.fromFXImage(capturedImage, null);
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(".png")) {
                        ImageIO.write(bImage, "png", file);
                    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        ImageIO.write(bImage, "jpg", file);
                    } else {
                        ImageIO.write(bImage, "png", file); // Mặc định lưu file dạng png
                    }
                    System.out.println("Image saved to: " + file.getAbsolutePath());

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to save image: " + e.getMessage());
                }
            } else {
                System.out.println("File save dialog was cancelled.");
            }
        });
    }

//    private void createFlashEffect() {
//        // Tạo một Rectangle màu trắng trên StackPane để tạo hiệu ứng flash
//        javafx.scene.shape.Rectangle flash = new javafx.scene.shape.Rectangle(stackPane.getWidth(), stackPane.getHeight(), javafx.scene.paint.Color.WHITE);
//        stackPane.getChildren().add(flash);
//
//        // Tạo hiệu ứng mờ dần cho flash
//        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), flash);
//        fadeTransition.setFromValue(1.0);
//        fadeTransition.setToValue(0.0);
//        fadeTransition.setOnFinished(e -> stackPane.getChildren().remove(flash));
//        fadeTransition.play();
//    }

    // Hiển thị thông báo lỗi khi chưa có ảnh chụp
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Image captureImage() {
        // Thực hiện chụp ảnh từ originalFrame hoặc nguồn hình ảnh của bạn
        // Đảm bảo originalFrame là kiểu phù hợp
        if (CameraFrame instanceof ImageView) {
            return ((ImageView) CameraFrame).getImage();
        } else {
            System.out.println("No image available to capture.");
            return null;
        }

    }

    @FXML
    void clickFilter(ActionEvent event) throws Exception {
//        startStopCamera();
//        Stage stage = (Stage) filter_button.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("FilterStage.fxml"));
//        stage.setTitle("Filter_Stage");
//        stage.setScene(new Scene(root));
    }

    @FXML
    void goBackMainStage(MouseEvent event) throws IOException {
        startStopCamera();
        Stage stage = (Stage) Go_Back_Button.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MainStage.fxml"));
        stage.setTitle("Main_Stage");
        stage.setScene(new Scene(root));
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
