package source.demofx;


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

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.opencv.core.Mat;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class FilterController extends Controller {
    @FXML
    private ImageView CameraFrame;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button cameraButton;
    @FXML
    private ToggleGroup filter;
    @FXML
    private RadioButton filter_button0;
    @FXML
    private RadioButton filter_button1;
    @FXML
    private RadioButton filter_button2;
    @FXML
    private RadioButton filter_button3;
    @FXML
    private RadioButton filter_button4;
    @FXML
    private RadioButton filter_button5;
    @FXML
    private RadioButton filter_button6;
    @FXML
    private ImageView filter_image0;
    @FXML
    private ImageView filter_image1;
    @FXML
    private ImageView filter_image2;
    @FXML
    private ImageView filter_image3;
    @FXML
    private ImageView filter_image4;
    @FXML
    private ImageView filter_image5;
    @FXML
    private ImageView filter_image6;


    private void setUp(){
        filter_image0.setOnMouseClicked(event -> {filter_button0.fire(); });
        filter_image1.setOnMouseClicked(event -> {filter_button1.fire(); });
        filter_image2.setOnMouseClicked(event -> {filter_button2.fire(); });
        filter_image3.setOnMouseClicked(event -> {filter_button3.fire(); });
        filter_image4.setOnMouseClicked(event -> {filter_button4.fire(); });
        filter_image5.setOnMouseClicked(event -> {filter_button5.fire(); });
        filter_image6.setOnMouseClicked(event -> {filter_button6.fire(); });
    }

    private int filterType = 0;
    protected VideoCapture cameraCapture;
    protected AtomicBoolean isCameraActive = new AtomicBoolean(false);

    private int getFilterType(){
        if(filter_button1.isSelected()) filterType = 1;
        else if(filter_button2.isSelected()) filterType = 2;
        else if(filter_button3.isSelected()) filterType = 3;
        else if(filter_button4.isSelected()) filterType = 4;
        else if(filter_button5.isSelected()) filterType = 5;
        else if(filter_button6.isSelected()) filterType = 6;
        else filterType = 0;
        return filterType;
    }
    @FXML
    protected void startStopCamera() {
        setUp();
        if (isCameraActive.get()) stopCamera();
        else startCamera();
    }

    protected void startCamera() {
        cameraCapture = new VideoCapture(0);

        final Mat[] frame = {new Mat()};

        anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            CameraFrame.setFitWidth(newVal.intValue());
        });
        anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            CameraFrame.setFitHeight(newVal.intValue());
        });

        isCameraActive.set(true);

        new Thread(() -> {
            while (isCameraActive.get() && cameraCapture.read(frame[0])) {
                if (frame[0].empty()) {
                    System.out.println("No detection");
                    break;
                } else {
                    try {
                        int val = getFilterType();
                        System.out.println(val);
                        if(val > 0){
                            FaceDetector faceDetector = new FaceDetector();
                            List<Rect> facesArray = faceDetector.detectFaces(frame[0]);
                            Filter filter = new Filter();
                            for (Rect face : facesArray)
                                frame[0] = filter.overlayImage(frame[0].clone(),val, face);

                        }
                        Platform.runLater(() -> {
                            MatOfByte mem = new MatOfByte();
                            Imgcodecs.imencode(".bmp", frame[0], mem);
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
//            stopCamera();
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
    protected void back(ActionEvent event) {
        super.back(event);
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}