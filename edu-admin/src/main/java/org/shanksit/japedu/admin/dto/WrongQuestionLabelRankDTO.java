package org.shanksit.japedu.admin.dto;

import lombok.Data;

/**
 * @author Kylin
 * @since
 */

@Data
public class WrongQuestionLabelRankDTO {
    String labelName;
    String studentClassName;
    Integer count;

}
