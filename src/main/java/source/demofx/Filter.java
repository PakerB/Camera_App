package source.demofx;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.util.List;
import java.util.ArrayList;

public class Filter {
    private Mat sticker;
    double[] Scale = {0,0.9,1.0,1.0,1.0,1.0,1.0};

    BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }

    private static Mat addAlphaChannel(Mat src) {
        if (src.channels() == 4) return src;
        Mat dst = new Mat(src.size(), CvType.CV_8UC4);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2BGRA);
        Mat alpha = new Mat(src.size(), CvType.CV_8UC1, new Scalar(255));
        List<Mat> bgraChannels = new ArrayList<>();
        Core.split(dst, bgraChannels);
        bgraChannels.set(3, alpha);
        Core.merge(bgraChannels, dst);
        return dst;
    }


    public Mat overlay(Mat background, Mat foreground, int x, int y){
        x = Math.max(0, x);
        y = Math.max(0, y);
        int w = Math.min(foreground.cols(), background.cols() - x);
        int h = Math.min(foreground.rows(), background.rows() - y);

        Rect roi = new Rect(x, y, w, h);
        Mat backgroundROI = background.submat(roi);
        Mat foregroundROI = foreground.submat(new Rect(0, 0, w, h));
        foregroundROI = addAlphaChannel(foregroundROI);
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

    public Mat overlayImage(Mat background, int filterType, Rect rect) {
        int stickerX1, stickerY1, stickerX2, stickerY2;
        stickerX1 = stickerY1 = stickerX2 = stickerY2 = 0;
//        if(filterType == 1) this.sticker = Imgcodecs.imread("src/main/resources/filterList/tai_meo.png");
//        else if (filterType == 2) this.sticker = Imgcodecs.imread("src/main/resources/filterList/vuong_niem.png");
//        else if (filterType == 3) this.sticker = Imgcodecs.imread("src/main/resources/filterList/message_iloveoop.png");
        if(filterType == 1) this.sticker = Imgcodecs.imread("D:\\fillter\\tai_meo.png");
        else if (filterType == 2) this.sticker = Imgcodecs.imread("D:\\fillter\\vuong_niem.png");
        else  this.sticker = Imgcodecs.imread("D:\\fillter\\message_iloveoop.png");

        // Resize sticker
        double filterScale = 0.9;//Scale[filterType];
        int stickerWidth = (int) (rect.width * filterScale);
        int stickerHeight = (int) (sticker.rows() * stickerWidth / sticker.cols());
        if(filterType != 4 && filterType != 5) {
            stickerX1 = rect.x + rect.width / 2 - stickerWidth / 2;
            stickerY1 = rect.y - stickerHeight;
        }
        Mat foreground = new Mat();
        Imgproc.resize(this.sticker, foreground, new Size(stickerWidth, stickerHeight));
        Mat output = overlay(background, foreground, stickerX1, stickerY1).clone();
        return output.clone();
    }


}