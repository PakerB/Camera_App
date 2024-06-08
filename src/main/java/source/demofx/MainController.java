package source.demofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class MainController {

    @FXML
    private Button Camera_Stage_Button;

    @FXML
    void goCameraStage(MouseEvent event) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CameraStage.fxml"));
//        Parent root = fxmlLoader.load();
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("Camera.css").toExternalForm());
//        CameraStageController cameraStageController = fxmlLoader.<CameraStageController>getController();
//        cameraStageController.startStopCamera();
//        Stage stage = (Stage) Camera_Stage_Button.getScene().getWindow();
//        stage.setTitle("Camera_Stage");
//        stage.setScene(new Scene(root));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CameraStage.fxml"));
        Parent root = fxmlLoader.load(); // Chỉ cần load một lần

        CameraStageController cameraStageController = fxmlLoader.getController();
        cameraStageController.startStopCamera(); // Gọi trước khi setScene

        // Tạo Scene và thêm CSS (nếu cần)
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("CameraStage.css").toExternalForm());

        // Lấy Stage hiện tại và cập nhật Scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Camera_Stage");
        stage.setScene(scene);
    }

    @FXML
    private Button Image_Stage_Button;
    @FXML
    void goImageStage(MouseEvent event) throws IOException{
        Stage stage = (Stage) Image_Stage_Button.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ImageStage.fxml"));
        stage.setTitle("Camera_Stage");
        stage.setScene(new Scene(root));
    }

}
