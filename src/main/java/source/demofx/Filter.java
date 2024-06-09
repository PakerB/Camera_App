package source.demofx;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter {
    @FXML
    private Button button_filter1;

    @FXML
    private Button button_filter2;
    @FXML
    private Button button_filter3;
    @FXML
    private Button button_filter4;
    @FXML
    protected ImageView filter1;
    @FXML
    protected ImageView filter2;
    @FXML
    protected ImageView filter3;
    @FXML
    protected ImageView filter4;

    protected Mat sticker;

    BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
    protected Mat overlayImage(Mat background, Mat foreground, int x, int y) {
        int w = Math.min(foreground.cols(), background.cols() - x);
        int h = Math.min(foreground.rows(), background.rows() - y);

        if (x < 0) {
            x = 0;
            w = foreground.cols() + x;
        }
        if (y < 0) {
            y = 0;
            h = foreground.rows() + y;
        }
        if (x + w > background.cols()) {
            w = background.cols() - x;
        }
        if (y + h > background.rows()) {
            h = background.rows() - y;
        }

        Rect roi = new Rect(x, y, w, h);
        Mat backgroundROI = background.submat(roi);
        Mat foregroundROI = foreground.submat(new Rect(0, 0, w, h));

        List<Mat> fgChannels = new ArrayList<>();
        Core.split(foregroundROI, fgChannels);
        Mat fgAlpha = fgChannels.get(3);

        Mat maskInv = new Mat();
        Core.bitwise_not(fgAlpha, maskInv);

        Mat fgWithoutAlpha = new Mat();
        List<Mat> fgWithoutAlphaChannels = new ArrayList<>(fgChannels);
        fgWithoutAlphaChannels.remove(3);
        Core.merge(fgWithoutAlphaChannels, fgWithoutAlpha);

        Mat bgWithMask = new Mat();
        Core.bitwise_and(backgroundROI, backgroundROI, bgWithMask, maskInv);

        Core.addWeighted(fgWithoutAlpha, 1.0, bgWithMask, 1.0, 0.0, backgroundROI);

        return background.clone();
    }

    // Abstract method to be implemented by subclasses
    public abstract void chooseFilter(Stage stage);

    @FXML
    public void clickFilter1(ActionEvent event) {
        // Set the sticker to the cat ears image
        this.sticker = Imgcodecs.imread("D:\\fillter\\tai_meo1.png", Imgcodecs.IMREAD_UNCHANGED);
        if (sticker.empty()) {
            System.out.println("Error: Could not load tai_meo.png image!");
        }else {
            System.out.println("Cat ears image loaded successfully.");
        }
        Stage stage = (Stage) button_filter1.getScene().getWindow();
        chooseFilter(stage);
        //System.out.println("Click Filter 1");
    }

    @FXML
    public void clickFilter2(ActionEvent event) {
        // You can set another sticker here if needed
        //chooseFilter();
        System.out.println("Click Filter 2");
    }

    @FXML
    public void clickFilter3(ActionEvent event) {
        // You can set another sticker here if needed
        //chooseFilter();
        System.out.println("Click Filter 3");
    }

    @FXML
    public void clickFilter4(ActionEvent event) {
        // You can set another sticker here if needed
        //chooseFilter();
        System.out.println("Click Filter 4");
    }
}
