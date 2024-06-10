package source.demofx;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ImageStageController extends FilterController{
    @FXML
    private Label lblnumber;
    @FXML
    private ImageView image;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane image_layout;
    public StackPane getImage_layout() {
        return image_layout;
    }
    public File copiedFile;
    public File selectedFile;
    public String imagePath;

    @FXML
    public void clickChoose() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        this.selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null ) {
            super.setUp();

            // Lưu đường dẫn của tệp ảnh đã chọn vào biến cục bộ
            this.imagePath = selectedFile.getAbsolutePath();
            String selectedFolderPath = "selected";

            File selectedFolder = new File(selectedFolderPath);
            if (!selectedFolder.exists()) {
                selectedFolder.mkdir();
            }

            this.copiedFile = new File(selectedFolderPath, "selected_image.jpg");

            try {
                Files.copy(Paths.get(imagePath), copiedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Mat originalImage = Imgcodecs.imread(copiedFile.getAbsolutePath());
            originalImage = resizeImage(originalImage,600,450);
            if (originalImage.empty()) {
                System.out.println("Không thể mở ảnh: " + copiedFile.getAbsolutePath());
                return;
            }
            Mat newImage = originalImage;
            FaceDetector faceDetector = new FaceDetector();
            List<Rect> facesArray = faceDetector.detectFaces(newImage);
            for (Rect face : facesArray) {
                Imgproc.rectangle(newImage, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(123, 213, 23, 220), 2);
                Imgproc.putText(newImage, "This is a person ", new Point(face.x, face.y - 20), 1, 1, new Scalar(255, 255, 255));
            }
            lblnumber.setText("Have " + facesArray.size());
            // Hiển thị ảnh đã chọn ban đầu
            BufferedImage bufferedImage = matToBufferedImage(newImage);
            WritableImage resultImage = SwingFXUtils.toFXImage(bufferedImage, null);

            image.setImage(resultImage);
            ImageZoomPane imageZoomPane = new ImageZoomPane(image);
            this.anchorPane.getChildren().add(imageZoomPane);
        }
    }
    @FXML
    public void setImage(ActionEvent event){
        int val = super.getFilterType();
        Mat originalImage = Imgcodecs.imread(copiedFile.getAbsolutePath());
        originalImage = resizeImage(originalImage,600,450);
        if (originalImage.empty()) {
            System.out.println("Không thể mở ảnh: " + copiedFile.getAbsolutePath());
            return;
        }
        Mat newImage = originalImage;
        FaceDetector faceDetector = new FaceDetector();
        List<Rect> facesArray = faceDetector.detectFaces(newImage);
        Filter filter = new Filter();

        for (Rect face : facesArray) {
            Point center = new Point((face.x + face.width * 0.5), (face.y + face.height * 0.5));
            if(val == 0){
                Imgproc.rectangle(newImage, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(123, 213, 23, 220), 2);
//                Imgproc.putText(newImage, "This is person ", new Point(face.x, face.y - 20), 1, 1, new Scalar(255, 255, 255));
            }
            else newImage = filter.overlayImage(newImage.clone(),val,face);
        }
        // Hiển thị ảnh đã chọn ban đầu
        BufferedImage bufferedImage = matToBufferedImage(newImage);
        WritableImage resultImage = SwingFXUtils.toFXImage(bufferedImage, null);
        this.image.setImage(resultImage);
    }
    @Override
    public void back(ActionEvent event) {
        super.back(event);
    }
    BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
    private Mat resizeImage(Mat image, int targetWidth, int targetHeight) {
        int width = image.width();
        int height = image.height();
        double ratio = Math.min((double) targetWidth / width, (double) targetHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, new org.opencv.core.Size(newWidth, newHeight));
        return resizedImage;
    }

}
