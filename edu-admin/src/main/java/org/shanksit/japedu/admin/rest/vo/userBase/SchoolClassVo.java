package org.shanksit.japedu.admin.rest.vo.userBase;

import lombok.Data;

import java.util.List;

/**
 * @author Kylin
 * @since
 */
@Data
public class SchoolClassVo {

    private Long schoolId;
    private List<Long> classIds;
}
