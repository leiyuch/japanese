package org.shanksit.japedu.common.util;

import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author: chenb
 * @date: 2022/4/15 16:05
 */
@Slf4j
public class ZipUtils {
    /**
     * 解压
     *
     * @param zipFilePath  带解压文件
     * @param desDirectory 解压到的目录
     * @throws Exception
     */
    public static void unzip(String zipFilePath, String desDirectory, Charset charset) throws Exception {

        File desDir = new File(desDirectory);
        if (!desDir.exists()) {
            boolean mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                ExceptionCast.cast(SystemErrorType.UNZIP_FILE_ERROR);
            }
        }
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath), charset);
        try {
            // 遍历每一个文件
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) { // 文件夹
                    String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                    // 直接创建
                    mkdir(new File(unzipFilePath));
                } else { // 文件
                    String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                    File file = new File(unzipFilePath);
                    // 如果文件存在，删除
                    if (file.exists()) {
                        file.delete();
                    }
                    // 创建父目录
                    mkdir(file.getParentFile());
                    // 写出文件流
                    BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                    byte[] bytes = new byte[1024];
                    int readLen;
                    while ((readLen = zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, readLen);
                    }
                    bufferedOutputStream.close();
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }

    // 如果父目录不存在则创建
    private static void mkdir(File file) {
        if (null == file || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdir();
    }

    /**
     * @param @param compressPath 需要压缩的文件夹的目录
     * @return String    返回类型
     * @throws
     * @Title: compressAllFileZip
     * @Description: 传递文件路径压缩文件，传递文件夹路径压缩文件夹，注：空的文件夹不会出现在压缩包内
     */
    public static String compressFileToZip(String compressPath) throws Exception {
        log.info("压缩路径：{}", compressPath);
        ZipOutputStream zipOutput = null;
        String zipPath;
        File file = new File(compressPath);
        if (!file.exists()) {
            throw new RuntimeException("压缩路径不存在");
        }
        if (file.isDirectory()) {
            zipPath = compressPath + ".zip";
            zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));
            compressZip(zipOutput, file, ""); // 递归压缩文件夹，最后一个参数传""压缩包就不会有当前文件夹；传file.getName(),则有当前文件夹;
        } else {
            zipPath = compressPath.substring(0, compressPath.lastIndexOf(".")) + ".zip";
            zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));
            zipOFile(zipOutput, file); // 压缩单个文件
        }
        zipOutput.closeEntry();
        zipOutput.close();
        return zipPath;
    }

    /**
     * @param @param  zipOutput
     * @param @param  file
     * @param @param  suffixpath
     * @param @throws IOException
     * @return void    返回类型
     * @throws
     * @Title: compressZip
     * @Description: 子文件夹中可能还有文件夹，进行递归
     */
    private static void compressZip(ZipOutputStream zipOutput, File file, String suffixpath) {
        File[] listFiles = file.listFiles();// 列出所有的文件
        for (File fi : listFiles) {
            if (fi.isDirectory()) {
                if ("".equals(suffixpath)) {
                    compressZip(zipOutput, fi, fi.getName());
                } else {
                    compressZip(zipOutput, fi, suffixpath + File.separator + fi.getName());
                }
            } else {
                zip(zipOutput, fi, suffixpath);
            }
        }
    }

    /**
     * @param @param zipOutput
     * @param @param file  文件
     * @param @param suffixpath  文件夹拼接路径
     * @return void    返回类型
     * @throws
     * @Title: zip
     * @Description: 压缩的具体操作
     */
    public static void zip(ZipOutputStream zipOutput, File file, String suffixpath) {
        try {
            ZipEntry zEntry = null;
            if ("".equals(suffixpath)) {
                zEntry = new ZipEntry(file.getName());
            } else {
                zEntry = new ZipEntry(suffixpath + File.separator + file.getName());
            }
            zipOutput.putNextEntry(zEntry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = bis.read(buffer)) != -1) {
                zipOutput.write(buffer, 0, read);
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param @param zipOutput
     * @param @param file  文件
     * @return void    返回类型
     * @throws
     * @Title: zip
     * @Description: 压缩单个文件
     */
    public static void zipOFile(ZipOutputStream zipOutput, File file) {
        try {
            ZipEntry zEntry = new ZipEntry(file.getName());
            zipOutput.putNextEntry(zEntry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = bis.read(buffer)) != -1) {
                zipOutput.write(buffer, 0, read);
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
