package org.shanksit.japedu.common.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Kylin
 * @since
 */

@MappedJdbcTypes(JdbcType.VARBINARY)
@MappedTypes({List.class})
public abstract class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, List<T> ts, JdbcType jdbcType) throws SQLException {
        String content = CollUtil.isEmpty(ts) ? null : JSON.toJSONString(ts);
        preparedStatement.setString(columnIndex,content);
    }

    @Override
    public List<T> getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return this.getListByJsonArrayString(resultSet.getString(columnName));
    }


    @Override
    public List<T> getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return this.getListByJsonArrayString(resultSet.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return this.getListByJsonArrayString(callableStatement.getString(columnIndex));
    }

    private List<T> getListByJsonArrayString(String content) {
        return StrUtil.isBlank(content) ? new ArrayList<>() : JSON.parseObject(content,this.specificType());
    }

    protected abstract TypeReference<List<T>> specificType();
}
