package source.demofx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

public class FilterController extends Controller {
    public void chooseFilter(Stage stage) {
        try {
            goCameraStage(stage);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void back(ActionEvent event, String pathFXML) {
        super.back(event);
    }

    void goCameraStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CameraStage.fxml"));
        Parent root = fxmlLoader.load();
        CameraStageController cameraStageController = fxmlLoader.<CameraStageController>getController();
        cameraStageController.startStopCamera();
        stage.setTitle("Camera_Stage");
        stage.setScene(new Scene(root));
    }
}
