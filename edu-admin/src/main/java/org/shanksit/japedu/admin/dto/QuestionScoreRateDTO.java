package org.shanksit.japedu.admin.dto;

import lombok.Data;

/**
 * 试题得分率
 * @author Kylin
 * @since
 */

@Data
public class QuestionScoreRateDTO {

    private Long questionId;
    private Integer rightNumber;
    private Integer totalNumber;
}
