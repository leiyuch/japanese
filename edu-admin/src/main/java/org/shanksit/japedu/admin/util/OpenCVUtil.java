package org.shanksit.japedu.admin.util;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.*;

public class OpenCVUtil {
	public static BufferedImage covertMat2Buffer(Mat mat) throws IOException {
		long time1 = new Date().getTime();
		// Mat 转byte数组
		BufferedImage originalB = toBufferedImage(mat);
		long time3 = new Date().getTime();
		System.out.println("保存读取方法2转=" + (time3 - time1));
		return originalB;
		// ImageIO.write(originalB, "jpg", new File("D:\\test\\testImge\\ws2.jpg"));
	}

	public static byte[] covertMat2Byte(Mat mat) throws IOException {
		long time1 = new Date().getTime();
		// Mat 转byte数组
		byte[] return_buff = new byte[(int) (mat.total() * mat.channels())];
		Mat mat1 = new Mat();
		mat1.get(0, 0, return_buff);
		long time3 = new Date().getTime();
		System.out.println(mat.total() * mat.channels());
		System.out.println("保存读取方法2转=" + (time3 - time1));
		return return_buff;
	}

	public static byte[] covertMat2Byte1(Mat mat) throws IOException {
		long time1 = new Date().getTime();
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, mob);
		long time3 = new Date().getTime();
		// System.out.println(mat.total() * mat.channels());
		System.out.println("Mat转byte[] 耗时=" + (time3 - time1));
		return mob.toArray();
	}

	public static BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	/**
	 * 腐蚀膨胀是针对于白色区域来说的，腐蚀即腐蚀白色区域
	 * 腐蚀算法（黑色区域变大）
	 * @param source
	 * @return
	 */
	public static Mat eroding(Mat source) {
		return eroding(source, 1);
	}

	public static Mat eroding(Mat source, double erosion_size) {
		Mat resultMat = new Mat(source.rows(), source.cols(), source.type());
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1,
				2 * erosion_size + 1));
		Imgproc.erode(source, resultMat, element);
		return resultMat;
	}

	/**
	 * 腐蚀膨胀是针对于白色区域来说的，膨胀是膨胀白色区域
	 * 膨胀算法（白色区域变大）
	 * @param source
	 * @return
	 */
	public static Mat dilation(Mat source) {
		return dilation(source, 1);
	}

	/**
	 * 腐蚀膨胀是针对于白色区域来说的，膨胀是膨胀白色区域
	 * @Author 王嵩
	 * @param source
	 * @param dilation_size 膨胀因子2*x+1 里的x
	 * @return Mat
	 * @Date 2018年2月5日
	 * 更新日志
	 * 2018年2月5日 王嵩  首次创建
	 *
	 */
	public static Mat dilation(Mat source, double dilation_size) {
		Mat resultMat = new Mat(source.rows(), source.cols(), source.type());
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1,
				2 * dilation_size + 1));
		Imgproc.dilate(source, resultMat, element);
		return resultMat;
	}

	/**
	 * 轮廓识别，使用最外轮廓发抽取轮廓RETR_EXTERNAL，轮廓识别方法为CHAIN_APPROX_SIMPLE
	 * @param source 传入进来的图片Mat对象
	 * @return 返回轮廓结果集
	 */
	public static Vector<MatOfPoint> findContours(Mat source) {
		Mat rs = new Mat();
		/**
		 * 定义轮廓抽取模式
		 *RETR_EXTERNAL:只检索最外面的轮廓;
		 *RETR_LIST:检索所有的轮廓，并将其放入list中;
		 *RETR_CCOMP:检索所有的轮廓，并将他们组织为两层:顶层是各部分的外部边界，第二层是空洞的边界;
		 *RETR_TREE:检索所有的轮廓，并重构嵌套轮廓的整个层次。
		 */
		int mode = Imgproc.RETR_EXTERNAL;
		// int mode = Imgproc.RETR_TREE;
		/**
		 * 定义轮廓识别方法
		 * 边缘近似方法(除了RETR_RUNS使用内置的近似，其他模式均使用此设定的近似算法)。可取值如下:
		 *CV_CHAIN_CODE:以Freeman链码的方式输出轮廓，所有其他方法输出多边形(顶点的序列)。
		 *CHAIN_APPROX_NONE:将所有的连码点，转换成点。
		 *CHAIN_APPROX_SIMPLE:压缩水平的、垂直的和斜的部分，也就是，函数只保留他们的终点部分。
		 *CHAIN_APPROX_TC89_L1，CV_CHAIN_APPROX_TC89_KCOS:使用the flavors of Teh-Chin chain近似算法的一种。
		 *LINK_RUNS:通过连接水平段的1，使用完全不同的边缘提取算法。使用CV_RETR_LIST检索模式能使用此方法。
		 */
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Vector<MatOfPoint> contours = new Vector<MatOfPoint>();
		Imgproc.findContours(source, contours, rs, mode, method, new Point());
		return contours;
	}


	/**
	 * py中imutils中经典的4点转换方法的java实现
	 * @param source
	 * @param listPoint
	 * @return Mat
	 *
	 */
	private static Mat fourPointTransform(Mat source,List<Point> listPoint) {
		//获得点的顺序
		List<Point> newOrderList = orderPoints(listPoint);
		for (Point point : newOrderList) {
			System.out.println(point);
		}
		//计算新图像的宽度，它将是右下角和左下角x坐标之间或右上角和左上角x坐标之间的最大距离
		//此处的顺序别搞错0,1,2,3依次是左上[0]，右上[1]，右下[2]，左下[3]
		Point leftTop = newOrderList.get(0);
		Point rightTop = newOrderList.get(1);
		Point rightBottom = newOrderList.get(2);
		Point leftBottom = newOrderList.get(3);
		double widthA = Math.sqrt(Math.pow(rightBottom.x-leftBottom.x, 2)
				+Math.pow(rightBottom.y-leftBottom.y, 2));
		double widthB = Math.sqrt(Math.pow(rightTop.x-leftTop.x, 2)
				+Math.pow(rightTop.y-leftTop.y, 2));
		int maxWidth = Math.max((int)widthA, (int)widthB);

		//计算新图像的高度，这将是右上角和右下角y坐标或左上角和左下角y坐标之间的最大距离，
		//这里用到的初中数学知识点和点的距离计算(x1,y1),(x2,y2)距离=√((x2-x1)^2+(y2-y1)^2)
		double heightA = Math.sqrt(Math.pow(rightTop.x-rightBottom.x, 2)
				+Math.pow(rightTop.y-rightBottom.y, 2));
		double heightB = Math.sqrt(Math.pow(leftTop.x-leftBottom.x, 2)
				+Math.pow(leftTop.y-leftBottom.y, 2));
		int maxHeight = Math.max((int)heightA, (int)heightB);
		System.out.println("宽度："+maxWidth);
		System.out.println("高度："+maxHeight);
		//现在我们指定目标图像的尺寸，构造目标点集以获得图像的“鸟瞰图”（即自上而下的视图），
		//再次指定左上角，右上角的点，右下角和左下角的顺序
		Point dstPoint1 = new Point(0,0);
		Point dstPoint2 = new Point(maxWidth-1,0);
		Point dstPoint3 = new Point(maxWidth-1,maxHeight-1);
		Point dstPoint4 = new Point(0,maxHeight-1);

		//计算透视变换矩阵rectMat原四顶点位置，dstMat目标顶点位置
		MatOfPoint2f rectMat = new MatOfPoint2f(leftTop,rightTop,rightBottom,leftBottom);
		MatOfPoint2f dstMat = new MatOfPoint2f(dstPoint1, dstPoint2, dstPoint3, dstPoint4);

		//opencv透视转换方法
		Mat transmtx = Imgproc.getPerspectiveTransform(rectMat, dstMat);
		//注意定义的新图像宽高设置
		Mat resultMat = Mat.zeros((int)maxHeight-1, (int)maxWidth-1, CvType.CV_8UC3);
		Imgproc.warpPerspective(source, resultMat, transmtx, resultMat.size());

		//返回矫正后的图像
		return resultMat;
	}

	/**
	 * 4点排序，四个点按照左上、右上、右下、左下组织返回
	 * @author song.wang
	 * @date 2019年8月16日
	 * @param listPoint
	 * @return List<Point>
	 *
	 * 更新日志
	 * 2019年8月16日 song.wang 首次创建
	 */
	private static List<Point> orderPoints(List<Point> listPoint) {
		//python中有很多关于数组的函数处理如排序、比较、加减乘除等，在这里我们使用List进行操作
		//如numpy.argsort;numpy.argmin;numpy.argmax;sum(axis = 1);diff(pts, axis = 1)等等，有兴趣的可以查阅相关资料
		//四个点按照左上、右上、右下、左下组织返回
		//直接在这里添加我们的排序规则,按照x坐标轴升序排列，小的放前面
		Collections.sort(listPoint, new Comparator<Point>() {
			public int compare(Point arg0, Point arg1) {
				if(arg0.x < arg1.x){
					return  -1;
				}else if (arg0.x> arg1.x){
					return 1;
				}else{
					return  0;
				}
			}
		});
		//排序之后前2个点就是左侧的点，后2个点为右侧的点
		//对比Y轴，y值小的是左上的点，y大的是左下的点
		Point top_left = new Point();
		Point bottom_left = new Point();
		Point top_right = new Point();
		Point bottom_right = new Point();

		Point leftPoint1 = listPoint.get(0);
		Point leftPoint2 = listPoint.get(1);
		Point rightPoint1 = listPoint.get(2);
		Point rightPoint2 = listPoint.get(3);
		if(leftPoint1.y > leftPoint2.y){
			top_left = leftPoint2;
			bottom_left = leftPoint1;
		}else{
			top_left = leftPoint1;
			bottom_left = leftPoint2;
		}
		//定位右侧的2个点右上和右下使用方法是毕达哥拉斯定理，就是勾股定理距离长的认为是右下角
		//计算左上方点和右侧两个点的欧氏距离
		//(y2-y1)^2+(x2-x1)^2 开根号
		double rightLength1 = Math.sqrt(Math.pow((rightPoint1.y - top_left.y), 2)
				+ Math.pow((rightPoint1.x - top_left.x), 2));
		double rightLength2 = Math.sqrt(Math.pow((rightPoint2.y - top_left.y), 2)
				+ Math.pow((rightPoint2.x - top_left.x), 2));
		if(rightLength1>rightLength2){
			//长度长的那个是右下角,短的为右上角；这个算法有一种情况会有可能出问题，比如倒梯形，但是在正常的俯角拍摄时不会出现这种情况
			//还有一种方案是按照左侧的那种对比方案，根据y轴的高度判断。
			top_right = rightPoint2;
			bottom_right = rightPoint1;
		}else{
			top_right = rightPoint1;
			bottom_right = rightPoint2;
		}
		//按照左上，右上，右下，左下的顺时针顺序排列，这点很重要，透视变换时根据这个顺序进行对应
		List<Point> newListPoint = new ArrayList<>();
		newListPoint.add(top_left);
		newListPoint.add(top_right);
		newListPoint.add(bottom_right);
		newListPoint.add(bottom_left);

		return newListPoint;
	}
}
