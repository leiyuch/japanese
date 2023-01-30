package org.shanksit.japedu.admin.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.Texts;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.vo.ExaminationQuestionBagVo;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器   工具类
 *
 * @author kylin
 * @date 2022-04-16 21:22:21
 */
public class GenUtils {


    /**
     * 模拟手动生成代码
     *
     * 模板地址必须是绝对路径
     *
     * @return ExaminationPaperEntity 试卷实体
     */
    public static ExaminationPaperEntity manualCode(ExaminationPaperEntity entity,
                                                    List<ExaminationQuestionBagVo> bagVoList,
                                                    List<String> imageNamesList,
                                                    String storePath,
                                                    String templatePath) throws IOException {

        //封装模板数据
        Map<String, Object> datas = new HashMap<String, Object>() {
            {
                put("paperName", Texts.of((entity.getPaperName())).bold().fontFamily("仿宋").fontSize(18).create());
                put("subPaperName", Texts.of((entity.getSubPaperName())).bold().fontFamily("仿宋").fontSize(14).create());
                put("questionBags", bagVoList);
            }
        };

        ConfigureBuilder builder = Configure.builder().useSpringEL();
        //模板地址
        XWPFTemplate template = XWPFTemplate.compile(templatePath, builder.build());

        template.render(datas);
        //判断是否有图 有图就写入图
        if (CollectionUtils.isEmpty(imageNamesList)) {
            //生成纯文字模板
            template.writeAndClose(new FileOutputStream(FileLocalUtils.pathCombine(storePath, entity.getPaperNo() + ".docx")));
        } else {
            //先生成文字模板 然后写入图片
            template.writeAndClose(new FileOutputStream(FileLocalUtils.pathCombine(storePath, entity.getPaperNo() + "_temp.docx")));
            XWPFTemplate template2 = XWPFTemplate.compile(FileLocalUtils.pathCombine(storePath, entity.getPaperNo() + "_temp.docx")).render(
                    new HashMap<String, Object>() {{
                        for (String imageNames : imageNamesList) {
                            String pathName = imageNames.substring(0, imageNames.lastIndexOf("."));
                            put(pathName, Pictures.ofStream(new FileInputStream(imageNames))
                                            .size(100, 120)
                                            .create());
                        }
                    }});

            template2.writeAndClose(new FileOutputStream(FileLocalUtils.pathCombine(storePath, entity.getPaperNo() + ".docx")));
        }
        System.out.println("******"+entity.getPaperNo() + ".docx");
        entity.setStorePath(FileLocalUtils.pathCombine(storePath, entity.getPaperNo() + ".docx"));
        return entity;
    }



}
