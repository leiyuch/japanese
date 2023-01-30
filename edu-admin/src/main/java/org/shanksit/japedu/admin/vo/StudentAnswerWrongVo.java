package org.shanksit.japedu.admin.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Kylin
 * @since
 */
@Data
public class StudentAnswerWrongVo {

    private String questionText;
    private String paperName;
    private String[] labelList;
    private String studentName;
    private Date reviewDate;
}
