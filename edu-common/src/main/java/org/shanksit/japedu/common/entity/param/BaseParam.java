package org.shanksit.japedu.common.entity.param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.shanksit.japedu.common.entity.po.BasePo;
import lombok.Data;

import java.util.Date;

/**
 * @author forlevinlee
 */
@Data
public class BaseParam<T extends BasePo> {
    private Date createdTimeStart;
    private Date createdTimeEnd;

    public QueryWrapper<T> build() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(null != this.createdTimeStart, "created_time", this.createdTimeStart)
                .le(null != this.createdTimeEnd, "created_time", this.createdTimeEnd);
        return queryWrapper;
    }
}
