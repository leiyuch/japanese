package org.shanksit.japedu.admin.util;

import org.shanksit.japedu.common.util.FileLocalUtils;

/**
 * 用来统一 文件名 目录生成
 *
 * @author Kylin
 * @since
 */

public class FileNameUtils {


    /**
     * 各题库下的带图片的试题 附带图片存储
     */

    public static String imagesStorePathGen(String imageBasicName, Long bankId, String imageName) {
        return FileLocalUtils.pathCombine(imageBasicName, String.valueOf(bankId), imageName);
    }

    /**
     * 各题库下的听力存储地址
     */
    public static String audioStorePathGen(String audioBasicName, Long bankId, String audioName) {
        return  FileLocalUtils.pathCombine(audioBasicName, "exception_question", String.valueOf(bankId), audioName);
    }

    /**
     * 各试卷拼接的音频的存储地址
     */
    public static String joinedAudioStorePathGen(String audioBasicName, String paperNo, String audioName) {
        return  FileLocalUtils.pathCombine(audioBasicName, "exception_paper", paperNo.concat("_").concat(audioName));
    }
}
