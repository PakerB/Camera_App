package source.demofx;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;


public class FaceDetector{

    String sourceFaceDetector = "C:\\Users\\hoang\\OneDrive\\Desktop\\k0d3\\OOP\\MaybeFinal\\src\\main\\resources\\source\\demofx\\haarcascades\\haarcascade_frontalface_default.xml";
    CascadeClassifier faceDetector = new CascadeClassifier((sourceFaceDetector));
    String sourceEyesDetector = "C:\\Users\\hoang\\OneDrive\\Desktop\\k0d3\\OOP\\MaybeFinal\\src\\main\\resources\\source\\demofx\\haarcascades\\haarcascade_eye.xml";
    CascadeClassifier eyesDetector = new CascadeClassifier((sourceEyesDetector));

    public List<Rect> detectFaces(Mat image) {
        Mat frame_gray = new Mat();
        MatOfRect rostros = new MatOfRect();
        Imgproc.cvtColor(image, frame_gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frame_gray, frame_gray);
        double w = image.width();
        double h = image.height();
        faceDetector.detectMultiScale(frame_gray, rostros, 1.1, 2, 0 | CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(w, h));
        Rect[] rects = rostros.toArray();

        List<Rect> faces = new ArrayList<>();
        for (Rect rect : rects) {
            Mat faceROI = frame_gray.submat(rect);
            MatOfRect eyes = new MatOfRect();
            eyesDetector.detectMultiScale(faceROI, eyes);
            if (eyes.toArray().length > 0) {
                faces.add(rect);
            }
        }
        return faces;
    }
    private String getPath(String source){
        String absolutePath = Objects.requireNonNull(FaceDetector.class.getResource(source)).getPath();
        {
            // Nạp tệp
            if (!faceDetector.load(absolutePath)) {
                System.err.println("Không thể nạp tệp từ " + absolutePath);
            } else {
                System.out.println("Đã nạp thành công tệp.");
            }
        }
        return absolutePath;
    }
}
