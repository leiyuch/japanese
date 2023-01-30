package org.shanksit.japedu.common.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kylin
 * @since
 */

public class ImageUtil {

    /**
     * 对图片进行原比例无损压缩,压缩后覆盖原图片
     *
     * @param path
     */
    public static void doWithPhoto(String path,String format) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        BufferedImage bufferedImage = null;
        FileOutputStream os = null;
        try {
            bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth() / 2;
            int height = bufferedImage.getHeight()/2;
            Image image = bufferedImage.getScaledInstance(width,height,Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics graphics = outputImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            ImageIO.write(outputImage, format, new File(path));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static void main(String[] args) {
        ImgUtil.scale(
                FileUtil.file("H:\\sanks\\japanese-edu\\image\\answer_sheet\\2022-09-11\\644110e0459e483fa445de574cd9b016.jpg"),
                FileUtil.file("H:\\sanks\\japanese-edu\\image\\answer_sheet\\2022-09-11\\644110e0459e483fa445de574cd9b016.jpg"),
                0.5f
        );
    }
}
