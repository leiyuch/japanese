package org.shanksit.japedu.admin.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author Kylin
 * @since
 */

@Data
public class StudentAnswerHistoryDto {

    private Long id;
    private Long examinationPaperId;
    private Date createdTime;
    private Long questionId;
    private String studentName;

}
