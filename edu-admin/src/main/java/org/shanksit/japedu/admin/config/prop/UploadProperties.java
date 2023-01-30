package org.shanksit.japedu.admin.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 登录筛选配置
 * @Author: chenb
 * @Date: 2021/9/9 22:13
 */
@Data
@Component
@ConfigurationProperties(prefix = "org.shanksit.upload")
public class UploadProperties implements Serializable {

    private static final long serialVersionUID = 6714712854735461293L;

    private String filePath;
    private String audioPath;
    private String imagePath;
    private long maxSize;
    private String fileSuffix;
}
