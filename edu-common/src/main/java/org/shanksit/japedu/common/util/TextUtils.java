package org.shanksit.japedu.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kylin
 * @since
 */

public class TextUtils {

    public static List<String> findImageNames(String text, String imagesBasePath, String serialNo, Long bankId, Map<String, String> imageNameMap) {
        int preIndex = 0;
        int aftIndex = 2;
        List<String> result = new ArrayList<>();

        while (true) {
            preIndex = text.indexOf("{{", preIndex);
            if (preIndex == -1) {
                break;
            }

            aftIndex = text.indexOf("}}", aftIndex);
            if (imageNameMap.containsKey(text.substring(preIndex + 2, aftIndex))) {
                result.add(FileLocalUtils.pathCombine(imagesBasePath, String.valueOf(bankId), serialNo, imageNameMap.get(text.substring(preIndex + 2, aftIndex))));
            } else{ //如果没有随文件一起上传 默认使用.jpg结尾
                result.add(FileLocalUtils.pathCombine(imagesBasePath, String.valueOf(bankId), serialNo, text.substring(preIndex + 2, aftIndex).concat(".jpg")));
            }
            preIndex = aftIndex;
            aftIndex = aftIndex + 2;
        }
        return result;
    }
}
