package org.shanksit.japedu.admin.dto;

import lombok.Data;

/**
 * @author Kylin
 * @since
 */

@Data
public class WrongQuestionRankDTO {
    private  Long questionId;
    private Integer wrongNumber;
}
