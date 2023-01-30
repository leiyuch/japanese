package org.shanksit.japedu.common.handler.po;

import com.alibaba.fastjson.TypeReference;
import org.shanksit.japedu.common.entity.vo.ParentQuestionVo;
import org.shanksit.japedu.common.handler.ListTypeHandler;

import java.util.List;

/**
 * @author Kylin
 * @since
 */

public class ParentQuestionListTypeHandler extends ListTypeHandler<ParentQuestionVo> {
    @Override
    protected TypeReference<List<ParentQuestionVo>> specificType() {
        return new TypeReference<List<ParentQuestionVo>>(){};
    }
}
