import org.opencv.core.Core;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * args[0] = path to folder with images for detecting
 *
 * @author Metenev-VV
 * @version 0.1
 * @since 0.1
 */

public class ObjectDetector {

    private final ObjectDetector objectDetector;

    public ObjectDetector(ObjectDetector objectDetector) {
        this.objectDetector = objectDetector;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Missing imagePath argument");
            System.exit(1);
        }
        //Start
        String imagePath = args[0];
        System.out.println("Hello, OpenCV");
        System.out.printf("\nPath to image: %s ", imagePath);

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    /**
     * @param image input image
     * @return an image has done Canny detector
     */
    private Mat getCannyImage(Mat image) {
        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();
        Mat resImg = new Mat();

        //convert to grayscale
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        //reduce noise
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        //canny detector with default thresholdes
        Imgproc.Canny(detectedEdges, resImg, 10, 100);

        return resImg;
    }


}

