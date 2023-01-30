package org.shanksit.japedu.admin.handler.opencv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.util.OpenCVUtil;
import org.shanksit.japedu.admin.util.WarpPerspectiveUtils;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import static org.opencv.imgproc.Imgproc.LINE_4;
import static org.opencv.imgproc.Imgproc.LINE_8;

/**
 * 答题卡识别
 * 当前答题卡使用统一样式
 * 输入来源是用户 拍照的答题卡样片
 * 要求答题卡不能放置在白色背景下
 * 答题卡需要有定位  -> 二期扩展  生成答题卡
 *
 * @author Kylin
 * @since
 */

@Service
@Slf4j
public class AnswerSheetHandler {
    @Autowired
    UploadProperties uploadProperties;




    /**
     * 执行  答题卡识别
     * 默认 传入图片是正放的 且 图片完全在相机拍摄范围内
     * 答题卡与背景严格区分
     * return 答题卡填涂详细
     */
    public TreeMap<Integer, String> execute(String fileName, String UUIDStr, String suffixName) {
        System.load(Core.NATIVE_LIBRARY_NAME);
        /**
         * 1. 透视变换
         * 从放置于高对比色背景下拍摄的答题卡图片 变换成俯视图
         */
        log.info("AnswerSheetHandler|| Step 1 Start|| times {}", System.currentTimeMillis());
        Mat img = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_COLOR);
        if (img.empty()) {
            ExceptionCast.cast(SystemErrorType.ANSWER_SHEET_IS_EMPTY);
        }
        //灰阶图
        Mat gray = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_GRAYSCALE);
        //高斯图
        Mat gaus = new Mat();
        Imgproc.GaussianBlur(gray, gaus, new Size(3, 3), 5);
        //双边滤波
        Mat bfilter = new Mat();
        Imgproc.bilateralFilter(gray, bfilter, 11, 17, 17);
        //边缘检测
        Mat edged = new Mat();
        Imgproc.Canny(bfilter, edged, 75, 200);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierachy = new Mat();
        //轮廓检测图
        Imgproc.findContours(edged, contours, hierachy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat contour_img = img.clone();
        Imgproc.drawContours(contour_img, contours, -1, new Scalar(0, 0, 255, 0), LINE_4, LINE_8, hierachy, 3, new Point(0, 0));
        //透视变换
        MatOfPoint2f approx = null;

        contours.sort(
                Comparator.comparingDouble(Imgproc::contourArea)
        );
        log.debug("寻找轮廓的个数{}", contours.size());

        for (MatOfPoint contour : contours) {
            MatOfPoint2f point2f = new MatOfPoint2f();
            contour.convertTo(point2f, CvType.CV_32F);
            // 周长，第1个参数是轮廓，第二个参数代表是否是闭环的图形
            double peri = Imgproc.arcLength(point2f, true);
            MatOfPoint2f temp = new MatOfPoint2f();
            // 获取多边形的所有定点，如果是四个定点，就代表是矩形 答题卡一定是矩形的
            Imgproc.approxPolyDP(point2f, temp, 0.02 * peri, true);
            // 打印顶点个数
            log.debug("顶点个数{},通道{},行数{}", temp.toList().size(), temp.rows(), temp.cols());
            if (temp.toList().size() == 4) {
                //透视变换提取原图内容部分
                approx = temp;
                break;
            }
        }

        //透视变换提取灰度图内容部分
        Mat sourceMat = WarpPerspectiveUtils.warpPerspective(img.clone(), approx);
        Mat sourceMatGray = WarpPerspectiveUtils.warpPerspective(gray.clone(), approx);
        log.info("AnswerSheetHandler|| Step 2 End|| times {}", System.currentTimeMillis());

        return answerCheck(sourceMat, sourceMatGray, 160, "16,60");


    }


    public TreeMap<Integer, String> answerCheck(Mat sourceMat, Mat sourceMatGray, Integer binary_thresh, String blue_red_thresh) {
        log.info("开始答案识别");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("先膨胀 后腐蚀");

        // 先膨胀 后腐蚀算法，开运算消除细小杂点
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * 1 + 1, 2 * 1 + 1));
        Imgproc.morphologyEx(sourceMatGray, sourceMatGray, Imgproc.MORPH_OPEN, element);
        stopWatch.stop();


        stopWatch.start("切割右侧和底部标记位图片");
        // 切割右侧和底部标记位图片
        Mat rightMark = new Mat(sourceMatGray, new Rect(sourceMatGray.cols() - 100, 0, 100, sourceMatGray.rows()));
        stopWatch.stop();


        stopWatch.start("平滑处理消除噪点毛刺等等");
        // 平滑处理消除噪点毛刺等等
        Imgproc.GaussianBlur(rightMark, rightMark, new Size(3, 3), 0);
        stopWatch.stop();


        stopWatch.start("根据右侧定位获取水平投影，并获取纵向坐标");
        // 根据右侧定位获取水平投影，并获取纵向坐标
        Mat matright = horizontalProjection(rightMark);
        stopWatch.stop();


        stopWatch.start("获取y坐标点，返回的是横向条状图集合");
        // 获取y坐标点，返回的是横向条状图集合
        List<Rect> listy = getBlockRect(matright, 1, 0);

        stopWatch.stop();
        stopWatch.start("获取底部");
        Mat footMark = new Mat(sourceMatGray, new Rect(0, sourceMatGray.rows() - 50, sourceMatGray.cols(), 50));

        stopWatch.stop();
        stopWatch.start("平滑处理后的底部定位点图");

        Imgproc.GaussianBlur(footMark, footMark, new Size(3, 3), 0);
        stopWatch.stop();


        stopWatch.start("根据底部定位获取垂直投影");
        // 根据底部定位获取垂直投影，并获取横向坐标
        Mat matbootom = verticalProjection(footMark);
        stopWatch.stop();

        stopWatch.start("获取x坐标点");
        // 获取x坐标点，返回的是竖向的柱状图集合
        List<Rect> listx = getBlockRect(matbootom, 0, 0);
        stopWatch.stop();
        stopWatch.start("增加HSV颜色查找");
        // 高阶处理：增加HSV颜色查找，查找红色像素点
        Mat matRed = findColorbyHSV(sourceMat, 156, 180);
        stopWatch.stop();


        stopWatch.start("灰度图");
        Mat dstNoRed = OpenCVUtil.dilation(sourceMatGray);
        stopWatch.stop();


        stopWatch.start("去除红色");
        Photo.inpaint(dstNoRed, matRed, dstNoRed, 1, Photo.INPAINT_NS);
        stopWatch.stop();


        stopWatch.start("灰度直方图图片1");
        Mat grayHistogram1 = getGrayHistogram(dstNoRed);
        stopWatch.stop();


        stopWatch.start("灰度直方图图片2");
        Mat answerMat = dstNoRed.submat(new Rect(41, 895, 278, 133));
        Mat grayHistogram2 = getGrayHistogram(answerMat);
        stopWatch.stop();


        stopWatch.start("去除红色基础上进行二值化");
        Imgproc.threshold(dstNoRed, dstNoRed, binary_thresh, 255, Imgproc.THRESH_BINARY_INV);
        stopWatch.stop();


        String redvalue = StringUtils.split(blue_red_thresh, ",")[0];
        String bluevalue = StringUtils.split(blue_red_thresh, ",")[1];
        log.debug(bluevalue + "			" + redvalue);
        TreeMap<Integer, String> resultMap = new TreeMap<Integer, String>();
        for (int no = 0; no < listx.size(); no++) {
            Rect rectx = listx.get(no);
            for (int an = 0; an < listy.size(); an++) {
                Rect recty = listy.get(an);
                Mat selectdst = new Mat(dstNoRed, new Range(recty.y, recty.y + recty.height), new Range(rectx.x,
                        rectx.x
                                + rectx.width));
                double p100 = Core.countNonZero(selectdst) * 100 / (selectdst.size().area());
                String que_answer = getQA(no, an);
                Integer que = Integer.valueOf(que_answer.split("_")[0]);
                String answer = que_answer.split("_")[1];
                log.debug(que_answer + ":			" + p100);

                if (p100 >= Integer.valueOf(bluevalue)) {// 蓝色
                    Imgproc.rectangle(sourceMat, new Point(rectx.x, recty.y), new Point(rectx.x + rectx.width, recty.y
                            + recty.height), new Scalar(255, 0, 0), 2);
                    // log.info(que_answer + ":填涂");
                    if (StringUtils.isNotEmpty(resultMap.get(que))) {
                        resultMap.put(que, resultMap.get(que) + "," + answer);
                    } else {
                        resultMap.put(que, answer);
                    }
                } else if (p100 > Integer.valueOf(redvalue) && p100 < Integer.valueOf(bluevalue)) {// 红色
                    Imgproc.rectangle(sourceMat, new Point(rectx.x, recty.y), new Point(rectx.x + rectx.width, recty.y
                            + recty.height), new Scalar(0, 0, 255), 2);
                    // log.info(que_answer + ":临界");
                    if (StringUtils.isNotEmpty(resultMap.get(que))) {
                        resultMap.put(que, resultMap.get(que) + "," + answer);
                    } else {
                        resultMap.put(que, answer);
                    }
                } else {// 绿色
                    Imgproc.rectangle(sourceMat, new Point(rectx.x, recty.y), new Point(rectx.x + rectx.width, recty.y
                            + recty.height), new Scalar(0, 255, 0), 1);
                    // log.info(que_answer + ":未涂");
                }
            }
        }

        return resultMap;

    }

    /**
     * 水平投影
     *
     * @param source 传入灰度图片Mat
     * @return
     */
    public static Mat horizontalProjection(Mat source) {
        Mat dst = new Mat(source.rows(), source.cols(), source.type());
        // 先进行反转二值化
        Imgproc.threshold(source, dst, 150, 255, Imgproc.THRESH_BINARY_INV);
        // 水平积分投影
        // 每一行的白色像素的个数
        int[] rowswidth = new int[dst.rows()];
        for (int i = 0; i < dst.rows(); i++) {
            for (int j = 0; j < dst.cols(); j++) {
                if (dst.get(i, j)[0] == 255) {
                    rowswidth[i]++;
                }
            }
        }
        // 定义一个白色跟原图一样大小的画布
        Mat matResult = new Mat(dst.rows(), dst.cols(), CvType.CV_8UC1, new Scalar(255, 255, 255));
        // 将每一行按照行像素值大小填充像素宽度
        for (int i = 0; i < matResult.rows(); i++) {
            for (int j = 0; j < rowswidth[i]; j++) {
                matResult.put(i, j, 0);
            }
        }
        return matResult;
    }

    /**
     * 垂直投影
     *
     * @param source 传入灰度图片Mat
     * @return
     */
    public static Mat verticalProjection(Mat source) {
        // 先进行反转二值化
        Mat dst = new Mat(source.rows(), source.cols(), source.type());
        Imgproc.threshold(source, dst, 150, 255, Imgproc.THRESH_BINARY_INV);
        // 垂直积分投影
        // 每一列的白色像素的个数
        int[] colswidth = new int[dst.cols()];
        for (int j = 0; j < dst.cols(); j++) {
            for (int i = 0; i < dst.rows(); i++) {
                if (dst.get(i, j)[0] == 255) {
                    colswidth[j]++;
                }
            }
        }
        Mat matResult = new Mat(dst.rows(), dst.cols(), CvType.CV_8UC1, new Scalar(255, 255, 255));
        // 将每一列按照列像素值大小填充像素宽度
        for (int j = 0; j < matResult.cols(); j++) {
            for (int i = 0; i < colswidth[j]; i++) {
                matResult.put(matResult.rows() - 1 - i, j, 0);
            }
        }
        return matResult;
    }

    public static List<Rect> getBlockRect(Mat srcImg, Integer proType, int rowXY) {
        Imgproc.threshold(srcImg, srcImg, 150, 255, Imgproc.THRESH_BINARY_INV);
        // 注意 countNonZero 方法是获取非0像素（白色像素）数量，所以一般要对图像进行二值化反转
        List<Rect> rectList = new ArrayList<Rect>();
        int size = proType == 0 ? srcImg.cols() : srcImg.rows();
        int[] pixNum = new int[size];
        if (proType == 0) {
            for (int i = 0; i < srcImg.cols(); i++) {
                Mat col = srcImg.col(i);
                pixNum[i] = Core.countNonZero(col) > 1 ? Core.countNonZero(col) : 0;
            }
        } else {// 水平投影只关注行
            for (int i = 0; i < srcImg.rows(); i++) {
                Mat row = srcImg.row(i);
                pixNum[i] = Core.countNonZero(row) > 1 ? Core.countNonZero(row) : 0;
            }
        }
        int startIndex = 0;// 记录进入字符区的索引
        int endIndex = 0;// 记录进入空白区域的索引
        boolean inBlock = false;// 是否遍历到了字符区内
        for (int i = 0; i < size; i++) {
            if (!inBlock && pixNum[i] != 0) {// 进入字符区，上升跳变沿
                inBlock = true;
                startIndex = i;
            } else if (pixNum[i] == 0 && inBlock) {// 进入空白区，下降跳变沿存储
                endIndex = i;
                inBlock = false;
                Rect rect = null;
                if (proType == 0) {
                    rect = new Rect(startIndex, rowXY, (endIndex - startIndex), srcImg.rows());
                } else {
                    rect = new Rect(rowXY, startIndex, srcImg.cols(), (endIndex - startIndex));
                }
                rectList.add(rect);
            }
        }
        return rectList;
    }

    public static Mat findBlackColorbyHSV(Mat source) {
        Mat hsv_image = new Mat();
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0, 0);
        Imgproc.cvtColor(source, hsv_image, Imgproc.COLOR_BGR2HSV);
        Mat thresholded = new Mat();
        Core.inRange(hsv_image, new Scalar(0, 0, 0), new Scalar(180, 255, 46), thresholded);
        return thresholded;
    }

    /**
     * 红色色系0-20，160-180
     * 蓝色色系100-120
     * 绿色色系60-80
     * 黄色色系23-38
     * 识别出的颜色会标记为白色，其他的为黑色
     *
     * @param min
     * @param max
     */
    public static Mat findColorbyHSV(Mat source, int min, int max) {
        Mat hsv_image = new Mat();
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0, 0);
        Imgproc.cvtColor(source, hsv_image, Imgproc.COLOR_BGR2HSV);
        Mat thresholded = new Mat();
        Core.inRange(hsv_image, new Scalar(min, 90, 90), new Scalar(max, 255, 255), thresholded);
        return thresholded;
    }

    public Mat getGrayHistogram(Mat img) {
        List<Mat> images = new ArrayList<Mat>();
        images.add(img);
        MatOfInt channels = new MatOfInt(0); // 图像通道数，0表示只有一个通道
        MatOfInt histSize = new MatOfInt(256); // CV_8U类型的图片范围是0~255，共有256个灰度级
        Mat histogramOfGray = new Mat(); // 输出直方图结果，共有256行，行数的相当于对应灰度值，每一行的值相当于该灰度值所占比例
        MatOfFloat histRange = new MatOfFloat(0, 255);
        Imgproc.calcHist(images, channels, new Mat(), histogramOfGray, histSize, histRange, false); // 计算直方图
        Core.MinMaxLocResult minmaxLoc = Core.minMaxLoc(histogramOfGray);
        // 按行归一化
        // Core.normalize(histogramOfGray, histogramOfGray, 0, histogramOfGray.rows(), Core.NORM_MINMAX, -1, new Mat());

        // 创建画布
        int histImgRows = 600;
        int histImgCols = 1300;
        System.out.println("---------" + histSize.get(0, 0)[0]);
        int colStep = (int) Math.floor(histImgCols / histSize.get(0, 0)[0]);// 舍去小数，不能四舍五入，有可能列宽不够
        Mat histImg = new Mat(histImgRows, histImgCols, CvType.CV_8UC3, new Scalar(255, 255, 255)); // 重新建一张图片，绘制直方图


        int max = (int) minmaxLoc.maxVal;
        System.out.println("--------" + max);
        double bin_u = (double) (histImgRows - 20) / max; // max: 最高条的像素个数，则 bin_u 为单个像素的高度，因为画直方图的时候上移了20像素，要减去
        int kedu = 0;
        for (int i = 1; kedu <= minmaxLoc.maxVal; i++) {
            kedu = i * max / 10;
            // 在图像中显示文本字符串
            Imgproc.putText(histImg, kedu + "", new Point(0, histImgRows - kedu * bin_u), 1, 1, new Scalar(0, 0, 0));
        }


        for (int i = 0; i < histSize.get(0, 0)[0]; i++) { // 画出每一个灰度级分量的比例，注意OpenCV将Mat最左上角的点作为坐标原点
            // System.out.println(i + ":=====" + histogramOfGray.get(i, 0)[0]);
            Imgproc.rectangle(histImg, new Point(colStep * i, histImgRows - 20), new Point(colStep * (i + 1), histImgRows
                            - bin_u * Math.round(histogramOfGray.get(i, 0)[0]) - 20),
                    new Scalar(0, 0, 0), 1, 8, 0);
            kedu = i * 10;
            // 每隔10画一下刻度
            Imgproc.rectangle(histImg, new Point(colStep * kedu, histImgRows - 20), new Point(colStep * (kedu + 1),
                    histImgRows - 20), new Scalar(255, 0, 0), 2, 8, 0);
            Imgproc.putText(histImg, kedu + "", new Point(colStep * kedu, histImgRows - 5), 1, 1, new Scalar(255, 0, 0)); // 附上x轴刻度
        }

        return histImg;

    }

    // 获取题号及选项填涂情况
    public String getQA(int no, int an) {
        //返回1A、1B、1C...2A类似这样的返回值
        int first = no + 1 + an / 4 * 20;
        String second = "";
        if (an % 4 == 0) {
            second = "A";
        } else if (an % 4 == 1) {
            second = "B";
        } else if (an % 4 == 2) {
            second = "C";
        } else if (an % 4 == 3) {
            second = "D";
        }
        return first + "_" + second;
    }

}
