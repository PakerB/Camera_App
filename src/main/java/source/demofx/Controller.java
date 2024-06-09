package source.demofx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller {
    protected Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    protected void back(ActionEvent event){
        try {
            // Thêm fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainStage.fxml"));
            Parent root = fxmlLoader.load(); // Chỉ cần load một lần
//            ImageStageController imageStageController = fxmlLoader.getController();

            Scene scene = new Scene(root);

            // Lấy Stage hiện tại và cập nhật Scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Choose Image");
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
