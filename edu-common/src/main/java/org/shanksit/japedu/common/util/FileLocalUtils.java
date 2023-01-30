package org.shanksit.japedu.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kylin
 * @since
 */

@Slf4j
public class FileLocalUtils {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String pathCombine(String path1, String... path2) {
        return Paths.get(path1, path2).toString();
//
    }

    /**
     * @param file     文件
     * @param fullPath 文件存放完整路径，包含文件名
     * @return
     */
    public static boolean upload(MultipartFile file, String fullPath) {
        File dest = new File(fullPath);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 如果文件存在，先删除
            if (dest.exists()) {
                dest.delete();
            }
            // 保存文件
            file.transferTo(dest);
            return true;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }


    /***
     * 获取目录下文件数量
     * @param folderPath
     * @return Integer
     * @author ChenBao
     * @date 2022/3/29 17:03
     */
    public static Integer lsNum(String folderPath) {
        return getFiles(folderPath).size();
    }

    /***
     * 获取目录下文件列表
     * @param folderPath
     * @return List<String>
     * @author ChenBao
     * @date 2022/3/29 17:03
     */
    public static List<String> getFiles(String folderPath) {
        try {
            File file = new File(folderPath);
            File[] array = file.listFiles();
            if (array == null) {
                return new ArrayList<>();
            }
            return Arrays.stream(array).map(o -> o.getName()).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("获取目录下文件列表-异常", ex);
            return new ArrayList<>();
        }
    }


    /***
     * 删除文件
     * @param fullPath
     * @return boolean
     * @author ChenBao
     * @date 2022/3/29 17:03
     */
    public static boolean delFile(String fullPath) {
        try {
            File file = new File(fullPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File subFile : files) {
                            subFile.delete();
                        }
                    }
                }
                return file.delete();
            }
            return true;
        } catch (Exception ex) {
            log.error("删除文件-异常", ex);
            return false;
        }
    }

    /***
     * 字符串写入文件
     * @param strFilename
     * @param strBuffer
     * @author ChenBao
     * @date 2022/4/15 21:28
     */
    public static void textToFile(final String strFilename, final String strBuffer) {
        try {
            // 创建文件对象
            File fileText = new File(strFilename);
            if (!fileText.getParentFile().exists()) {
                fileText.getParentFile().mkdirs();
            }
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(fileText);
            // 写文件
            fileWriter.write(strBuffer);
            // 关闭
            fileWriter.close();
        } catch (IOException e) {
            log.error("字符串写入文件-异常", e);
        }
    }

    /***
     * 字符串写入文件
     * @param strFilename
     * @param strBuffer
     * @author ChenBao
     * @date 2022/4/15 21:28
     */
    public static void textToFileAppend(final String strFilename, final String strBuffer) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(strFilename);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(strBuffer);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            log.error("字符串写入文件-异常", e);
        }
    }

    /***
     * 字符串写入文件
     * @param targetFile
     * @param strBuffer
     * @author ChenBao
     * @date 2022/4/15 21:28
     */
    public static void textToFile(final File targetFile, final String strBuffer) {
        try {
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(targetFile);
            // 写文件
            fileWriter.write(strBuffer);
            // 关闭
            fileWriter.close();
        } catch (IOException e) {
            log.error("字符串写入文件-异常", e);
        }
    }

    /**
     * 创建好目录
     *
     * @param fullPath
     */
    public static void createFileFolder(String fullPath) {
        File dest = new File(fullPath);
        dest.mkdirs();
    }


    /**
     * 获取文件名称前缀
     *
     * @param fileName
     * @return
     */
    public static String getPrefix(String fileName, boolean toLower) {
        if (fileName.contains(".")) {
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            if (toLower) {
                return trimilSpace(prefix.toLowerCase());
            }
            return trimilSpace(prefix);
        }
        return fileName;
    }

    public static String trimilSpace(String input) {
        if (input == null) {
            return null;
        }
        String result = input.replaceAll("\u00A0", "").replaceAll("\u200B", "").replaceAll("\u2002", "")
                .replaceAll("\u200C", "").replaceAll("\u200D", "").replaceAll("\uFEFF", "").trim();
        if ("".equals(input)) {
            return null;
        }
        return result;
    }

    public static String fileReader(String fullPath) {
        try {
            File file = new File(fullPath);
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            String strAll = "";
            while ((str = in.readLine()) != null) {
                strAll += str;
            }
            if (!StringUtils.isBlank(str)) {
                strAll += str;
            }
            return strAll;
        } catch (IOException e) {
            return null;
        }
    }

    public static String fileReader(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            String strAll = "";
            while ((str = in.readLine()) != null) {
                strAll += str;
            }
            if (!StringUtils.isBlank(str)) {
                strAll += str;
            }
            return strAll;
        } catch (IOException e) {
            return null;
        }
    }

    public static void fileDown(String downPath, HttpServletResponse response) throws Exception {
        log.info("下载路径：{}", downPath);
        FileInputStream in = null; // 输入流
        ServletOutputStream out = null; // 输出流
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            File file = new File(downPath);
            if (!file.exists()) {
                response.getWriter().print("下载路径不存在！");
                return;
            }
            // 设置下载文件使用的报头
            response.setHeader("Content-Type", "application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + toUTF8String(file.getName()));
            // 读入文件
            in = new FileInputStream(downPath);
            // 得到响应对象的输出流，用于向客户端输出二进制数据
            out = response.getOutputStream();
            out.flush();
            int aRead = 0;
            byte b[] = new byte[1024];
            while ((aRead = in.read(b)) != -1 & in != null) {
                out.write(b, 0, aRead);
            }
            out.flush();
            in.close();
            out.close();
        } catch (Exception e) {
            throw new Exception("下载失败", e);
        }
    }

    /**
     * 下载保存时中文文件名的字符编码转换方法
     */
    private static String toUTF8String(String str) {
        StringBuffer sb = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            // 取出字符中的每个字符
            char c = str.charAt(i);
            // Unicode码值为0~255时，不做处理
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else { // 转换 UTF-8 编码
                byte b[];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    b = null;
                }
                // 转换为%HH的字符串形式
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) {
                        k &= 255;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
}

