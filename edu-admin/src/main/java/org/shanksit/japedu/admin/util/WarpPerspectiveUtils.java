package org.shanksit.japedu.admin.util;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.List;

/**
 * 透视变换工具类
 * 因为我透视变换做的也不是很好，就仅提供一个大概的函数...
 */
public class WarpPerspectiveUtils {

    /**
     *  矩形顶点排序
     * @param pointList
     * @return
     */
    public static List<Point> sort4Point(List<Point> pointList) {
        // 点的顺序[左上 ，右上 ，右下 ，左下]

        return pointList;
    }

    /**
     * 透视变换
     *
     * @param src
     * @param points
     * @return
     */
    public static Mat warpPerspective(Mat src, Point[] points) {
        // 点的顺序[左上 ，右上 ，右下 ，左下]
        List<Point> listSrcs = Arrays.asList(
                points[0],
                points[1],
                points[2],
                points[3]
        );
        Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

        List<Point> listDsts = Arrays.asList(
                new Point(0, 0),
                new Point(src.width(), 0),
                new Point(src.width(), src.height()),
                new Point(0, src.height())
        );


        Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

        Mat perspectiveMmat = Imgproc.getPerspectiveTransform(dstPoints, srcPoints);

        Mat dst = new Mat();

        Imgproc.warpPerspective(
                src,
                dst,
                perspectiveMmat,
                src.size(),
                Imgproc.INTER_LINEAR + Imgproc.WARP_INVERSE_MAP,
                1,
                new Scalar(0)
        );

        return dst;
    }


    /**
     * 透视变换
     *  0,y   x,y
     *
     *  0,0   x,0
     *
     * @param src
     * @param point2f
     * @return
     */
    public static Mat warpPerspective(Mat src, MatOfPoint2f point2f) {
        // 点的顺序[左上 ，右上 ，右下 ，左下]

        List<Point> listDsts = Arrays.asList(
                new Point(0, 0),
                new Point(0, src.height()),
                new Point(src.width(), src.height()),
                new Point(src.width(), 0)
        );

        Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

        Mat perspectiveMmat = Imgproc.getPerspectiveTransform(dstPoints, point2f);

        Mat dst = new Mat();

        Imgproc.warpPerspective(
                src,
                dst,
                perspectiveMmat,
                src.size(),
                Imgproc.INTER_LINEAR + Imgproc.WARP_INVERSE_MAP,
                1,
                new Scalar(0)
        );

        return dst;
    }


}
