package org.shanksit.japedu.admin.dto;

import lombok.Data;

/**
 * @author Kylin
 * @since
 */

@Data
public class WrongQuestionClassRankDTO {
    Long questionId;
    Long studentClass;
    String studentClassName;
    Integer WrongNumber;

}
