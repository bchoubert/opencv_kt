import jdk.nashorn.internal.runtime.JSType
import org.opencv.core.*
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


class Main {
    companion object {
        fun loadImage(path: String): Mat {
            val image: Mat

            // Load the image
            image = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR)

            // Controls the image
            if (image.empty() || image.cols() == 0 || image.rows() == 0) {
                System.err.println("The image at the path $path cannot be loaded")
            }

            return image
        }

        fun detectCicles(image: Mat) {

            // Transform the image in grayscale then blur it a bit
            val gray: Mat = Mat()
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)
            Imgproc.blur(gray, gray, Size(3.0, 3.0))
            Imgproc.medianBlur(gray, gray, 5)

            // Transform to canny
            val cannyMat: Mat = Mat()
            Imgproc.Canny(gray, cannyMat, 40.0, 120.0)

            // Circle detection
            val circles: Mat = Mat()
            Imgproc.HoughCircles(cannyMat, circles, Imgproc.CV_HOUGH_GRADIENT, 0.1, gray.rows() / 16.0, 20.0, 120.0, 1, 0)

            // Fetch circles one by one
            for (i in 0 .. circles.cols()) {
                val c = circles.get(0, i)
                if (c != null) {
                    val center = Point(c[0], c[1])

                    // Draw the circle center
                    Imgproc.circle(image, center, 1, Scalar(0.0, 255.0, 0.0), -1)

                    // Draw the circle outline
                    Imgproc.circle(image, center, JSType.toInt32(c[2]), Scalar(255.0, 0.0, 255.0, 1.0), 2)
                }
            }
        }

        fun detectContours(image: Mat) {
            val contours: List<MatOfPoint> = ArrayList()

            // Tranform to canny
            val cannyMat: Mat = Mat()
            Imgproc.Canny(image, cannyMat, 40.0, 120.0)

            // Find contours
            Imgproc.findContours(cannyMat, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

            // Draw contours to original image
            for (i in 0 .. (contours.size - 1)) {
                Imgproc.drawContours(image, contours, i, Scalar(255.0, 255.0, 0.0), 1)
            }
        }
    }
}

fun main(args: Array<String>) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    println("Using OpenCV v"+Core.VERSION)

    val image2euro: Mat = Main.loadImage("resources/2euro.jpg")
    Main.detectCicles(image2euro)
    Main.detectContours(image2euro)
    HighGui.imshow("Image detection : 2 euros", image2euro)

    val imageLimite90: Mat = Main.loadImage("resources/limite90.png")
    Main.detectCicles(imageLimite90)
    Main.detectContours(imageLimite90)
    HighGui.imshow("Image detection : speed limit", imageLimite90)

    val imageBalls: Mat = Main.loadImage("resources/balls.jpg")
    Main.detectCicles(imageBalls)
    Main.detectContours(imageBalls)
    HighGui.imshow("Image detection : balls", imageBalls)

    HighGui.waitKey(0)
}