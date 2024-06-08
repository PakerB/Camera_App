package source.demofx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ImageStageController {

    @FXML
    private Button Hello;

    @FXML
    private Button Image_Stage_Button;

    @FXML
    void SayHello(ActionEvent event) {
        System.out.println("Hello");
    }

    @FXML
    void goBackMainStage(MouseEvent event) throws IOException {
        Stage stage = (Stage) Image_Stage_Button.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MainStage.fxml"));
        stage.setTitle("Main_Stage");
        stage.setScene(new Scene(root));
    }

}
